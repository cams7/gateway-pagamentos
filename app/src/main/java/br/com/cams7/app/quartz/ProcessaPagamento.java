/**
 * 
 */
package br.com.cams7.app.quartz;

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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.cams7.app.itau.ItauParametro;
import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;
import br.com.cams7.app.model.entity.Pedido.SituacaoPagamento;

/**
 * @author cesaram
 *
 */
public abstract class ProcessaPagamento {

	protected final Logger LOG = Logger.getLogger(getClass().getSimpleName());

	protected final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

	private final int HTTP_OK = 200;

	@EJB
	private PedidoRepository pedidoRepository;

	protected void processaPagamento(Pedido pedido) {
		try {
			Document document = getRespostaItau(1, pedido.getId());
			List<ItauParametro> parametros = getItauParametros(document);

			if (parametros != null)
				for (ItauParametro parametro : parametros) {
					Long pedidoId = Long.valueOf(parametro.getPedido());

					if (pedidoId.equals(pedido.getId())) {
						Float valorPago = Float.valueOf(parametro.getValor());
						FormaPagamento formaPagamento = FormaPagamento.getFormaPagamento(parametro.getTipPag());
						SituacaoPagamento situacaoPagamento = SituacaoPagamento
								.getSituacaoPagamento(parametro.getSitPag());

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

				}

			LOG.log(Level.INFO, "O pedido {0} foi processado...", new Object[] { SDF.format(pedido.getDataPedido()) });
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	private List<ItauParametro> getItauParametros(Document document) {
		if (document == null)
			return null;

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
						} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException
								| NoSuchMethodException | SecurityException e) {
							LOG.log(Level.SEVERE, e.getMessage());
						}
					}
				}

				itauParametros.add(itauParametro);
			}
		}

		return itauParametros;
	}

	private Document getRespostaItau(Integer codigoEmpresa, Long codigoPedido) throws IOException {
		final String USER_AGENT = "Mozilla/5.0";

		final String URL = "http://localhost:8090/gateway-pagamentos-api/consulta";

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL);

		// add header
		request.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("DC", String.format("%s;%s;00", codigoPedido, codigoEmpresa)));

		request.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(request);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HTTP_OK) {
			InputStream content = response.getEntity().getContent();

			try {
				Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(content);
				return document;
			} catch (SAXException | ParserConfigurationException e) {
				LOG.log(Level.SEVERE, e.getMessage());
			}
		} else
			LOG.log(Level.WARNING, "O código de estado HTTP ({0}) é inválido", new Object[] { statusCode });

		return null;

	}

	protected PedidoRepository getPedidoRepository() {
		return pedidoRepository;
	}

}
