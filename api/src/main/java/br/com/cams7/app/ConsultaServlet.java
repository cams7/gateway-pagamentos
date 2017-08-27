package br.com.cams7.app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/consulta")
public class ConsultaServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		service(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		service(request, response);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String DC = request.getParameter("DC");

		String numeroPedido = getParam(DC, 0);
		// String codigoEmpresa = getParam(DC, 1);
		// String formado = getParam(DC, 2);

		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// out.append("<!DOCTYPE consulta[");
		// out.append("<!ELEMENT consulta(PARAMETER)>");
		// out.append("<!ELEMENT PARAMETER(PARAM)+>");
		// out.append("<!ELEMENT PARAM EMPTY>");
		// out.append("<!ATTLIST PARAM ID CDATA #REQUIRED>");
		// out.append("VALUE CDATA #REQUIRED>");
		// out.append("]>");

		out.append("<consulta>");
		out.append("<PARAMETER>");

		try {
			PagamentoDAO pagamentoDAO = new PagamentoDAOImpl();

			Pagamento pagamento = pagamentoDAO.buscaPeloPedido(numeroPedido);

			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "CodEmp",
					getField(pagamento.getCodigoEmpresa())));
			out.append(
					String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "Pedido", getField(pagamento.getNumeroPedido())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "Valor",
					getField(pagamento.getValorPagamento())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "tipPag",
					getField(pagamento.getTipoPagamento())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "sitPag",
					getField(pagamento.getSituacaoPagamento())));
			out.append(
					String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "dtPag", getField(pagamento.getDataPagamento())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "codAut",
					getField(pagamento.getCodigoAutorizacao())));
			out.append(
					String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "numId", getField(pagamento.getNsuTransacao())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "compVend",
					getField(pagamento.getComprovanteVenda())));
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "tipCard",
					getField(pagamento.getTipoCartaoCredito())));
		} catch (AppException e) {
			out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "ERRO", getField(e.getMessage())));
		}

		out.append("</PARAMETER>");
		out.append("</consulta>");
	}

	private String getParam(final String DC, int i) {
		if (DC == null)
			return "";

		String[] params = DC.split(";");
		String param = params[i];
		return param;
	}

	private Object getField(Object field) {
		if (field == null)
			return "";

		return field;
	}

}
