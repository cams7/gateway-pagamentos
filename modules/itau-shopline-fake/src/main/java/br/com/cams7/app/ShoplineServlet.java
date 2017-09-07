/**
 * 
 */
package br.com.cams7.app;

import static br.com.cams7.app.itau.ApiShopline.getNumeroPedido;
import static br.com.cams7.app.itau.ApiShopline.getValorPagamento;
import static br.com.cams7.app.itau.Pagamento.TipoPagamento.A_VISTA;
import static br.com.cams7.app.itau.Pagamento.TipoPagamento.BOLETO;
import static br.com.cams7.app.itau.Pagamento.TipoPagamento.CARTAO_CREDITO;
import static br.com.cams7.app.itau.Pagamento.TipoPagamento.NAO_ESCOLHIDO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Itau.Itaucripto;
import Itau.Shopline;
import br.com.cams7.app.itau.Pagamento;
import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;
import br.com.cams7.app.util.AppConfig;
import br.com.cams7.app.util.AppException;

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
		String dc = request.getParameter("DC");

		Shopline shopline;
		if (dc != null) {
			final String CHAVE_CRIPTOGRAFIA = AppConfig.getProperty("API_CHAVE_CRIPTOGRAFIA");

			Itaucripto cripto = new Itaucripto();
			dc = cripto.decripto(dc, CHAVE_CRIPTOGRAFIA);
			shopline = cripto.getShopline();
		} else
			shopline = new Shopline();

		final String PARAM_CODIGO_EMPRESA = "codigo_empresa";
		final String PARAM_NUMERO_PEDIDO = "numero_pedido";
		final String PARAM_VALOR_PAGAMENTO = "valor_pagamento";

		String codigoEmpresa = getParam(request.getParameter(PARAM_CODIGO_EMPRESA), shopline.getCodigoEmpresa());
		Long numeroPedido = getNumeroPedido(
				getParam(request.getParameter(PARAM_NUMERO_PEDIDO), shopline.getNumeroPedido()));
		Double valorPagamento = getValorPagamento(
				getParam(request.getParameter(PARAM_VALOR_PAGAMENTO), shopline.getValorPagamento()));

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
			pagamento.setDataPagamento(new Date());

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

		PagamentoDAO pagamentoDAO = new PagamentoDAOImpl();
		TipoPagamento tipoPagamento = pagamentoDAO.buscaTipoPagamentoPeloPedido(numeroPedido);

		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_NAO_ESCOLHIDO + "\" "
				+ disabledField(tipoPagamento, NAO_ESCOLHIDO) + ">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_A_VISTA + "\" "
				+ disabledField(tipoPagamento, A_VISTA) + ">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMETO_BOLETO + "\" "
				+ disabledField(tipoPagamento, BOLETO) + ">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_PAGAMENTO_CARTAO_CREDITO + "\" "
				+ disabledField(tipoPagamento, CARTAO_CREDITO) + ">");

		out.println("</DIV>");
		out.println("</FORM>");
		out.println("</body>");
		out.println("</html>");
	}

	private String disabledField(TipoPagamento tipoInformado, TipoPagamento tipoPagamento) {
		return tipoInformado == null || tipoPagamento.equals(tipoInformado) ? "" : "disabled";
	}

	private String getParam(String param, final String shoplineValue) {
		if (param != null)
			return param;

		return shoplineValue;
	}

	private void processaPagamentoNaoEscolhido(Pagamento pagamento) {
		pagamento.setTipoPagamento(NAO_ESCOLHIDO);
		incluiPagamento(pagamento, new String[] { "01", "02", "03" }, new String[] { "03" });
	}

	private void processaPagamentoAVista(Pagamento pagamento) {
		pagamento.setTipoPagamento(A_VISTA);
		incluiPagamento(pagamento, new String[] { "00", "01", "02", "03" }, new String[] { "00", "03" });
	}

	private void processaPagamentoCartaoCredito(Pagamento pagamento) {
		pagamento.setTipoPagamento(CARTAO_CREDITO);
		incluiPagamento(pagamento, new String[] { "00", "01", "02", "03" }, new String[] { "00", "03" });
	}

	private void processaPagamentoBoleto(Pagamento pagamento) {
		pagamento.setTipoPagamento(BOLETO);
		incluiPagamento(pagamento, new String[] { "00", "01", "02", "03", "04", "05", "06" },
				new String[] { "00", "03", "04", "05", "06" });

	}

	private void incluiPagamento(Pagamento pagamento, String[] situacoesInclusao, String[] situacoesAlteracao) {
		try {
			PagamentoDAO pagamentoDAO = new PagamentoDAOImpl();

			if (!pagamentoDAO.existePagamento(pagamento.getNumeroPedido())) {
				pagamento.setSituacaoPagamento(getSituacaoPagamento(situacoesInclusao));
				pagamentoDAO.cadastra(pagamento);
			} else if (pagamentoDAO.seraProcessadoNovamente(pagamento.getNumeroPedido())) {
				pagamento.setSituacaoPagamento(getSituacaoPagamento(situacoesAlteracao));
				pagamentoDAO.atualiza(pagamento.getNumeroPedido(), pagamento.getSituacaoPagamento());
			} else
				System.err.println("O pagamento já foi cadastrado anteriormente");
		} catch (AppException e) {
			System.err.println(e);
		}
	}

	private SituacaoPagamento getSituacaoPagamento(String[] situacoes) {
		int index = situacoes.length > 1 ? new Random().nextInt(situacoes.length - 1) : 0;
		return SituacaoPagamento.getSituacaoPagamento(situacoes[index]);
	}

}
