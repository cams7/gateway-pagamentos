/**
 * 
 */
package br.com.cams7.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cesaram
 *
 */
@SuppressWarnings("serial")
@WebServlet("/shopline")
public class ShoplineServlet extends HttpServlet {

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

		final String PARAM_CODIGO_EMPRESA = "codigo_empresa";
		final String PARAM_NUMERO_PEDIDO = "numero_pedido";
		final String PARAM_VALOR_PAGAMENTO = "valor_pagamento";

		String codigoEmpresa = getParam(request.getParameter(PARAM_CODIGO_EMPRESA), DC, 0);
		String numeroPedido = getParam(request.getParameter(PARAM_NUMERO_PEDIDO), DC, 1);
		String valorPagamento = getParam(request.getParameter(PARAM_VALOR_PAGAMENTO), DC, 2);

		final String BUTTON_PAGAMENTO_NAO_ESCOLHIDO = "Não escolhido";
		final String BUTTON_PAGAMENTO_A_VISTA = "À vista";
		final String BUTTON_PAGAMETO_BOLETO = "Boleto";
		final String BUTTON_PAGAMENTO_CARTAO_CREDITO = "Cartão de crédito";

		String action = request.getParameter("action");

		if (action != null) {
			Pagamento pagamento = new Pagamento();
			pagamento.setNumeroPedido(numeroPedido);
			pagamento.setCodigoEmpresa(codigoEmpresa);
			pagamento.setValorPagamento(valorPagamento);
			pagamento.setDataPagamento(new SimpleDateFormat("ddMMyyyy").format(new Date()));

			switch (action) {
			case BUTTON_PAGAMENTO_A_VISTA:
				processaPagamentoAVista(pagamento);
				break;
			case BUTTON_PAGAMETO_BOLETO:
				processaPagamentoBoleto(pagamento);
				break;
			case BUTTON_PAGAMENTO_CARTAO_CREDITO:
				processaPagamentoCartaoCredito(pagamento);
				break;
			default:
				processaPagamentoNaoEscolhido(pagamento);
				break;
			}
		}

		PrintWriter out = response.getWriter();

		response.setContentType("text/html");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Teste Itaú Shopline </title>");
		out.println("</head>");
		out.println("<body  style=\"text-align:center;\">");
		out.println("<h1>Teste Itaú Shopline</h1>");
		// Constrói o formulário para pagamento com shopline
		out.println("<FORM METHOD=\"POST\">");
		out.println("<DIV>");
		out.println("<TABLE align=\"center\">");
		out.println("<TR>");
		out.println("<TD>Pedido:</TD>");
		out.println("<TD>");
		out.println(
				"<INPUT TYPE=\"text\" NAME=\"" + PARAM_NUMERO_PEDIDO + "\" VALUE=\"" + numeroPedido + "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("<TR>");
		out.println("<TD>Empresa:</TD>");
		out.println("<TD>");
		out.println(
				"<INPUT TYPE=\"text\" NAME=\"" + PARAM_CODIGO_EMPRESA + "\" VALUE=\"" + codigoEmpresa + "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("<TR>");
		out.println("<TD>Valor:</TD>");
		out.println("<TD>");
		out.println("<INPUT TYPE=\"text\" NAME=\"" + PARAM_VALOR_PAGAMENTO + "\" VALUE=\"" + valorPagamento
				+ "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("</TABLE>");
		out.println("</DIV>");
		out.println("<DIV>");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_NAO_ESCOLHIDO + "\">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_A_VISTA + "\">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMETO_BOLETO + "\">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_CARTAO_CREDITO + "\">");
		out.println("</DIV>");
		out.println("</FORM>");
		out.println("</body>");
		out.println("</html>");
	}

	private String getParam(String param, final String DC, int i) {
		if (param != null)
			return param;

		if (DC == null)
			return "";

		String[] params = DC.split(";");
		param = params[i];
		return param;
	}

	private void processaPagamentoNaoEscolhido(Pagamento pagamento) {
		String[] situacoes = new String[] { "01", "02", "03" };

		pagamento.setTipoPagamento("00");
		pagamento.setSituacaoPagamento(situacoes[new Random().nextInt(situacoes.length - 1)]);

		incluiPagamento(pagamento);
	}

	private void processaPagamentoAVista(Pagamento pagamento) {
		String[] situacoes = new String[] { "00", "01", "02", "03" };

		pagamento.setTipoPagamento("01");
		pagamento.setSituacaoPagamento(situacoes[new Random().nextInt(situacoes.length - 1)]);

		incluiPagamento(pagamento);
	}

	private void processaPagamentoBoleto(Pagamento pagamento) {
		String[] situacoes = new String[] { "00", "01", "02", "03", "04", "05", "06" };

		pagamento.setTipoPagamento("02");
		pagamento.setSituacaoPagamento(situacoes[new Random().nextInt(situacoes.length - 1)]);

		incluiPagamento(pagamento);

	}

	private void processaPagamentoCartaoCredito(Pagamento pagamento) {
		String[] situacoes = new String[] { "00", "01", "02", "03" };

		pagamento.setTipoPagamento("03");
		pagamento.setSituacaoPagamento(situacoes[new Random().nextInt(situacoes.length - 1)]);

		incluiPagamento(pagamento);
	}

	private void incluiPagamento(Pagamento pagamento) {
		try {
			PagamentoDAO pagamentoDAO = new PagamentoDAOImpl();

			if (!pagamentoDAO.existePagamento(pagamento.getNumeroPedido()))
				pagamentoDAO.cadastra(pagamento);
			else
				System.out.println("O pagamento já foi cadastrado anteriormente");
		} catch (AppException e) {
			System.err.println(e);
		}
	}

}
