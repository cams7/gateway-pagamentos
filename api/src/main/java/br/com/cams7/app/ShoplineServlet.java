/**
 * 
 */
package br.com.cams7.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

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
		System.out.println("DC: " + DC);

		final String PARAM_COD_PEDIDO = "Pedido";
		final String PARAM_COD_EMP = "CodEmp";
		final String PARAM_VALOR = "Valor";

		String codigoPedido = getParam(request.getParameter(PARAM_COD_PEDIDO), DC, 0);
		String codigoEmpresa = getParam(request.getParameter(PARAM_COD_EMP), DC, 1);
		String valorPedido = getParam(request.getParameter(PARAM_VALOR), DC, 2);

		System.out.println("Empresa: " + codigoEmpresa + ", pedido: " + codigoPedido + ", valor: " + valorPedido);

		final String BUTTON_DOWN_PAYMENT = "Á vista";
		final String BUTTON_CREDIT_CARD = "Cartão";
		final String BUTTON_BANK_SLIP = "Boleto";

		String action = request.getParameter("action");

		if (action != null)
			switch (action) {
			case BUTTON_DOWN_PAYMENT:
				downPaymentProcess(codigoPedido, codigoEmpresa, valorPedido);
				break;
			case BUTTON_CREDIT_CARD:
				creditCardProcess(codigoPedido, codigoEmpresa, valorPedido);
				break;
			default:
				bankSlipProcess(codigoPedido, codigoEmpresa, valorPedido);
				break;
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
		out.println("<INPUT TYPE=\"text\" NAME=\"" + PARAM_COD_PEDIDO + "\" VALUE=\"" + codigoPedido + "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("<TR>");
		out.println("<TD>Empresa:</TD>");
		out.println("<TD>");
		out.println("<INPUT TYPE=\"text\" NAME=\"" + PARAM_COD_EMP + "\" VALUE=\"" + codigoEmpresa + "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("<TR>");
		out.println("<TD>Valor:</TD>");
		out.println("<TD>");
		out.println("<INPUT TYPE=\"text\" NAME=\"" + PARAM_VALOR + "\" VALUE=\"" + valorPedido + "\" readonly>");
		out.println("</TD>");
		out.println("</TR>");
		out.println("</TABLE>");
		out.println("</DIV>");
		out.println("<DIV>");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_DOWN_PAYMENT + "\">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_CREDIT_CARD + "\">");
		out.println("<INPUT TYPE=\"submit\" name=\"action\" value=\"" + BUTTON_BANK_SLIP + "\">");
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

	private void downPaymentProcess(String codigoPedido, String codigoEmpresa, String valorPedido) {
		addPayment(codigoPedido, codigoEmpresa, valorPedido, "00");
	}

	private void creditCardProcess(String codigoPedido, String codigoEmpresa, String valorPedido) {
		addPayment(codigoPedido, codigoEmpresa, valorPedido, "01");
	}

	private void bankSlipProcess(String codigoPedido, String codigoEmpresa, String valorPedido) {
		addPayment(codigoPedido, codigoEmpresa, valorPedido, "02");

	}

	private void addPayment(String codigoPedido, String codigoEmpresa, String valorPedido, String tipoPagamento) {
		Connection conn = null;
		try {
			// conn = DriverManager.getConnection(URL);
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/app");
			conn = ds.getConnection();

			String situacaoPagamento = "00";
			String dataPagamento = new SimpleDateFormat("ddMMyyyy").format(new Date());

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement(
						"insert into PEDIDO (COD_PEDIDO, COD_EMP, COD_EMP, TIP_PAG, SIT_PAG, DT_PAG) VALUES(?, ?, ?, ?, ?, ?)");

				ps.setString(1, codigoPedido);
				ps.setString(2, codigoEmpresa);
				ps.setDouble(3, Double.valueOf(valorPedido));
				ps.setString(4, tipoPagamento);
				ps.setString(5, situacaoPagamento);
				ps.setString(6, dataPagamento);

				// execute insert SQL stetement
				ps.executeUpdate();

				System.out.println("Record is inserted into DBUSER table!");
			} finally {
				if (ps != null) {
					ps.close();
				}
			}

		} catch (SQLException | NamingException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

}
