/**
 * 
 */
package br.com.cams7.app.schedule.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
import br.com.cams7.app.itau.Pagamento;
import br.com.cams7.app.itau.Parametro;
import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.TipoPagamento;
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

		String codigoEmpresa = "J0000560680005480000000013";

		Document document = getRespostaItau(codigoEmpresa, pedido.getId());
		List<Pagamento> pagamentos = getPagamentos(document);

		for (Pagamento pagamento : pagamentos) {
			if (!pagamento.getNumeroPedido().equals(pedido.getId()))
				throw new AppException(String.format(
						"O número do pedido (%s), retornado pelo Banco Itaú, não é o mesmo número (%s) que está cadastrado na base de dados",
						pagamento.getNumeroPedido(), pedido.getId()));

			pedido.setValorPago(pagamento.getValorPagamento());
			pedido.setTipoPagamento(pagamento.getTipoPagamento());
			pedido.setSituacaoPagamento(pagamento.getSituacaoPagamento());
			pedido.setDataPagamento(pagamento.getDataPagamento());

			getPedidoRepository().atualiza(pedido);
		}

		if (!pagamentos.isEmpty())
			LOG.log(Level.INFO, "O pedido ({0}) foi processado...", new Object[] { pedido.getId() });

	}

	private List<Pagamento> getPagamentos(Document document) {
		Element eConsulta = document.getDocumentElement();

		NodeList parameters = eConsulta.getElementsByTagName("PARAMETER");

		List<Pagamento> pagamentos = new ArrayList<>();

		for (int i = 0; i < parameters.getLength(); i++) {
			Node parameter = parameters.item(i);

			if (parameter.getNodeType() == Node.ELEMENT_NODE) {
				Element eParameter = (Element) parameter;

				NodeList params = eParameter.getElementsByTagName("PARAM");

				Pagamento pagamento = new Pagamento();

				int length = params.getLength();

				if (length == 1) {
					Node param = params.item(0);

					if (param.getNodeType() == Node.ELEMENT_NODE) {
						Element eParam = (Element) param;

						final String ID = eParam.getAttribute("ID").toUpperCase();
						final String VALUE = eParam.getAttribute("VALUE");

						if (ID.equals("ERRO"))
							throw new AppException(VALUE);
					}
				}

				for (int j = 0; j < length; j++) {
					Node param = params.item(j);

					if (param.getNodeType() == Node.ELEMENT_NODE) {
						Element eParam = (Element) param;

						final String ID = eParam.getAttribute("ID").toUpperCase();
						final String VALUE = eParam.getAttribute("VALUE");

						try {
							for (Field field : Pagamento.class.getDeclaredFields())
								if (field.isAnnotationPresent(Parametro.class)
										&& ID.equals(field.getAnnotation(Parametro.class).value())) {
									field.setAccessible(true);
									field.set(pagamento, getParamValue(field.getType(), VALUE));
									break;
								}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new AppException(e);
						}

					}
				}

				pagamentos.add(pagamento);
			}
		}

		return pagamentos;
	}

	private Object getParamValue(final Class<?> fieldType, final String value) {
		if (Long.class.equals(fieldType))
			return Long.valueOf(value);

		if (Double.class.equals(fieldType))
			return Double.valueOf(value.replaceFirst(",", "."));

		if (TipoPagamento.class.equals(fieldType))
			return TipoPagamento.getTipoPagamento(value);

		if (SituacaoPagamento.class.equals(fieldType))
			return SituacaoPagamento.getSituacaoPagamento(value);

		if (Date.class.equals(fieldType)) {
			// Numérico com 8 posições no formato "ddmmaaaa"
			String diaDoMes = value.substring(0, 2);
			String mes = value.substring(2, 4);
			String ano = value.substring(4);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(diaDoMes));
			calendar.set(Calendar.MONTH, Integer.valueOf(mes) - 1);
			calendar.set(Calendar.YEAR, Integer.valueOf(ano));

			return calendar.getTime();
		}

		return value;
	}

	private Document getRespostaItau(String codigoEmpresa, Long codigoPedido) {
		final String USER_AGENT = "Mozilla/5.0";

		final String URL = "http://localhost:8090/gateway-pagamentos-api/consulta";

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL);

		// add header
		request.setHeader("User-Agent", USER_AGENT);

		// Formato: Formato do retorno da consulta
		// Numérico com 01 posição:
		// 0 para formato de página HTML para consulta visual
		// 1 para formato XML
		final String FORMATO = "1";

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("DC",
				String.format("%s;%s;%s", codigoPedido, codigoEmpresa.toUpperCase(), FORMATO)));

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
