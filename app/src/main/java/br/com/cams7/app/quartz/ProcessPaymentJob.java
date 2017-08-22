package br.com.cams7.app.quartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.OrderRepository;
import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.PaymentMethod;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessPaymentJob implements Job {

	private static final Logger LOG = Logger.getLogger(ProcessPaymentJob.class.getSimpleName());
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

	public static String PAYMENT_METHOD = "PaymentMethod";

	@EJB
	private OrderRepository orderRepository;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			PaymentMethod paymentMethod = (PaymentMethod) data.get(PAYMENT_METHOD);

			Order order = orderRepository.findByPaymentMethod(paymentMethod);
			if (order != null)
				processPayment(order);

			LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
					new Object[] { context.getTrigger().getKey(), SDF.format(context.getFireTime()),
							context.getScheduler().getSchedulerInstanceId() });
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	private void processPayment(Order order) {
		try {
			sendPost(1, order.getId());
			LOG.log(Level.INFO, "O pedido {} foi processado...", SDF.format(order.getOrderDate()));
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	// HTTP POST request
	private void sendPost(Integer codigoEmpresa, Long codigoPedido) throws IOException {
		final String USER_AGENT = "Mozilla/5.0";

		final String URL = "http://localhost:8090/gateway-pagamentos-api/consulta";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(URL);

		// add header
		request.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("DC", String.format("%s;%s;00", codigoPedido, codigoEmpresa)));

		request.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(request);
		LOG.info("\nSending 'POST' request to URL : " + URL);
		LOG.info("Post parameters : " + request.getEntity());
		LOG.info("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null)
			result.append(line);

		LOG.info(result.toString());

	}
}
