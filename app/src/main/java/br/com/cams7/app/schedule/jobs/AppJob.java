/**
 * 
 */
package br.com.cams7.app.schedule.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.cams7.app.beans.PedidosEncontradosBean;
import br.com.cams7.app.itau.ItauParametro;
import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;
import br.com.cams7.app.model.entity.Pedido.SituacaoPagamento;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;
import br.com.cams7.app.util.AppException;

/**
 * @author cesaram
 *
 */
public abstract class AppJob {

	protected final Logger LOG = Logger.getLogger(getClass().getSimpleName());

	private final int HTTP_OK = 200;

	@Inject
	private PedidosEncontradosBean pedidosEncontrados;

	@EJB
	private PedidoRepository pedidoRepository;

	protected void processaPedido(Pedido pedido) {
		Document document = getRespostaItau(1, pedido.getId());
		List<ItauParametro> parametros = getItauParametros(document);

		for (ItauParametro parametro : parametros) {
			Long pedidoId = Long.valueOf(parametro.getPedido());

			if (!pedidoId.equals(pedido.getId()))
				throw new AppException(String.format(
						"O número do pedido (%s), retornado pelo Banco Itaú, não é o mesmo número (%s) que está cadastrado na base de dados",
						pedidoId, pedido.getId()));

			Float valorPago = Float.valueOf(parametro.getValor());
			FormaPagamento formaPagamento = FormaPagamento.getFormaPagamento(parametro.getTipPag());
			SituacaoPagamento situacaoPagamento = SituacaoPagamento.getSituacaoPagamento(parametro.getSitPag());

			String diaDoMes = parametro.getDtPag().substring(0, 2);
			String mes = parametro.getDtPag().substring(2, 4);
			String ano = parametro.getDtPag().substring(4);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(diaDoMes));
			calendar.set(Calendar.MONTH, Integer.valueOf(mes) - 1);
			calendar.set(Calendar.YEAR, Integer.valueOf(ano));

			Date dataPagamento = calendar.getTime();

			pedido.setValorPago(valorPago);
			pedido.setFormaPagamento(formaPagamento);
			pedido.setSituacaoPagamento(situacaoPagamento);
			pedido.setDataPagamento(dataPagamento);

			getPedidoRepository().atualiza(pedido);
		}

		LOG.log(Level.INFO, "O pedido ({0}) foi processado...", new Object[] { pedido.getId() });

	}

	private List<ItauParametro> getItauParametros(Document document) {
		Element eConsulta = document.getDocumentElement();

		// element.normalize();
		NodeList parameters = eConsulta.getElementsByTagName("PARAMETER");

		List<ItauParametro> itauParametros = new ArrayList<>();

		for (int i = 0; i < parameters.getLength(); i++) {
			Node parameter = parameters.item(i);

			if (parameter.getNodeType() == Node.ELEMENT_NODE) {
				Element eParameter = (Element) parameter;

				NodeList params = eParameter.getElementsByTagName("PARAM");

				ItauParametro itauParametro = new ItauParametro();

				for (int j = 0; j < params.getLength(); j++) {
					Node param = params.item(j);

					if (param.getNodeType() == Node.ELEMENT_NODE) {
						Element eParam = (Element) param;

						final String ID = eParam.getAttribute("ID");
						final String VALUE = eParam.getAttribute("VALUE");

						String fieldName = "set" + ID.substring(0, 1).toUpperCase() + ID.substring(1);

						try {
							ItauParametro.class.getMethod(fieldName, String.class).invoke(itauParametro, VALUE);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							throw new AppException(e);
						}

					}
				}

				itauParametros.add(itauParametro);
			}
		}

		return itauParametros;
	}

	private Document getRespostaItau(Integer codigoEmpresa, Long codigoPedido) {
		final String USER_AGENT = "Mozilla/5.0";

		final String URL = "http://localhost:8090/gateway-pagamentos-api/consulta";

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL);

		// add header
		request.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("DC", String.format("%s;%s;00", codigoPedido, codigoEmpresa)));

		try {
			request.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HTTP_OK)
				throw new AppException(String.format("O código de estado HTTP (%s) é inválido", statusCode));

			InputStream content = response.getEntity().getContent();

			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content);
			return document;

		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new AppException(e);
		}
	}

	protected void pauseOrRestartJob(Scheduler scheduler, JobKey jobKey, boolean pause) throws SchedulerException {
		boolean paused = isPaused(scheduler, jobKey);

		if (pause) {
			if (!paused)
				scheduler.pauseJob(jobKey);
		} else if (paused)
			scheduler.resumeJob(jobKey);

	}

	private boolean isPaused(Scheduler scheduler, JobKey jobKey) throws SchedulerException {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		for (Trigger trigger : triggers) {
			TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

			if (TriggerState.PAUSED.equals(triggerState))
				return true;
		}

		return false;
	}

	protected void showJobLog(JobExecutionContext context) throws SchedulerException {
		LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
				new Object[] { context.getTrigger().getKey(),
						new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(context.getFireTime()),
						context.getScheduler().getSchedulerInstanceId() });
	}

	protected static String getCarregaName(TarefaId rotina) {
		return getJobName("carrega-" + rotina.getCodigo());
	}

	protected static String getProcessaName(TarefaId rotina) {
		return getJobName("processa-" + rotina.getCodigo());
	}

	private static String getJobName(String name) {
		return name + "-job";
	}

	protected PedidoRepository getPedidoRepository() {
		return pedidoRepository;
	}

	protected PedidosEncontradosBean getPedidosEncontrados() {
		return pedidosEncontrados;
	}

}
