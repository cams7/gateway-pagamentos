/**
 * 
 */
package br.com.cams7.app.itau;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import Itau.Itaucripto;
import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;
import br.com.cams7.app.util.AppConfig;
import br.com.cams7.app.util.AppException;

/**
 * @author César Magalhães
 *
 */
public class ApiShopline {

	public static final int HTTP_OK = 200;

	public static List<Pagamento> getPagamentos(final Long numeroPedido) {
		Document document = getRespostaItau(numeroPedido);
		return getPagamentos(document);
	}

	private static List<Pagamento> getPagamentos(Document document) {
		Element eConsulta = document.getDocumentElement();

		NodeList parameters = eConsulta.getElementsByTagName("PARAMETER");

		List<Pagamento> pagamentos = new ArrayList<>();

		for (int i = 0; i < parameters.getLength(); i++) {
			Node parameter = parameters.item(i);

			if (parameter.getNodeType() == Node.ELEMENT_NODE) {
				Element eParameter = (Element) parameter;

				NodeList params = eParameter.getElementsByTagName("PARAM");

				Pagamento pagamento = new Pagamento();

				for (int j = 0; j < params.getLength(); j++) {
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

	private static Object getParamValue(final Class<?> fieldType, final String value) {
		if (Long.class.equals(fieldType))
			return getNumeroPedido(value);

		if (Double.class.equals(fieldType))
			return getValorPagamento(value);

		if (TipoPagamento.class.equals(fieldType))
			return getTipoPagamento(value);

		if (SituacaoPagamento.class.equals(fieldType))
			return getSituacaoPagamento(value);

		if (Date.class.equals(fieldType)) {
			return getDataPagamento(value);
		}

		return value;
	}

	public static Long getNumeroPedido(String numeroPedido) {
		return Long.valueOf(numeroPedido);
	}

	public static String getNumeroPedido(Long numeroPedido) {
		return String.valueOf(numeroPedido);
	}

	public static Double getValorPagamento(final String valorPagamento) {
		return Double.valueOf(valorPagamento.replaceFirst(",", "."));
	}

	public static String getValorPagamento(final Double valorPagamento) {
		return new DecimalFormat("#.00").format(valorPagamento);
	}

	public static TipoPagamento getTipoPagamento(final String tipoPagamento) {
		return TipoPagamento.getTipoPagamento(tipoPagamento);
	}

	public static String getTipoPagamento(final TipoPagamento tipoPagamento) {
		return tipoPagamento.getCodigoItau();
	}

	public static SituacaoPagamento getSituacaoPagamento(final String situacaoPagamento) {
		return SituacaoPagamento.getSituacaoPagamento(situacaoPagamento);
	}

	public static String getSituacaoPagamento(final SituacaoPagamento situacaoPagamento) {
		return situacaoPagamento.getCodigoItau();
	}

	public static Date getDataPagamento(final String value) {
		String diaDoMes = value.substring(0, 2);
		String mes = value.substring(2, 4);
		String ano = value.substring(4);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(diaDoMes));
		calendar.set(Calendar.MONTH, Integer.valueOf(mes) - 1);
		calendar.set(Calendar.YEAR, Integer.valueOf(ano));

		return calendar.getTime();
	}

	public static String getDataPagamento(Date dataPagamento) {
		return new SimpleDateFormat("ddMMyyyy").format(dataPagamento);
	}

	private static Document getRespostaItau(final Long numeroPedido) {
		final String CODIGO_EMPRESA = AppConfig.getProperty("API_CODIGO_EMPRESA");
		final String URL_CONSULTA = AppConfig.getProperty("API_URL_CONSULTA");
		final String CHAVE_CRIPTOGRAFIA = AppConfig.getProperty("API_CHAVE_CRIPTOGRAFIA");
		final String FORMATO_CONSULTA = AppConfig.getProperty("API_FORMATO_CONSULTA");

		Itaucripto cripto = new Itaucripto();
		String dc = cripto.geraConsulta(CODIGO_EMPRESA, getNumeroPedido(numeroPedido), FORMATO_CONSULTA,
				CHAVE_CRIPTOGRAFIA);

		final String USER_AGENT = "Mozilla/5.0";

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL_CONSULTA);

		// add header
		request.setHeader("User-Agent", USER_AGENT);

		// Formato: Formato do retorno da consulta
		// Numérico com 01 posição:
		// 0 para formato de página HTML para consulta visual
		// 1 para formato XML

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("DC", dc));

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

}
