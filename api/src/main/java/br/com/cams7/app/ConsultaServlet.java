package br.com.cams7.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

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
		System.out.println("DC: " + DC);

		String codigoPedido = getParam(DC, 0);
		String codigoEmpresa = getParam(DC, 1);
		String formado = getParam(DC, 2);

		System.out.println("Empresa: " + codigoEmpresa + ", pedido: " + codigoPedido + ", formato: " + formado);

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

		Connection conn = null;
		try {
			// conn = DriverManager.getConnection(URL);
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/app");
			conn = ds.getConnection();

			System.out.println("Connection to SQLite has been established.");

			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(
						"select p.COD_PEDIDO, p.COD_EMP, p.VALOR, p.TIP_PAG, p.SIT_PAG, p.DT_PAG, p.COD_AUT, p.NUM_ID, p.COMP_VEND, p.TIP_CARD from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setString(1, codigoPedido);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					codigoPedido = rs.getString(1);
					codigoEmpresa = rs.getString(2);
					Double valorPedido = rs.getDouble(3);
					String tipoPagamento = rs.getString(4);
					String situacaoPagamento = rs.getString(5);
					String dataPagamento = rs.getString(6);
					String codAut = rs.getString(7);
					String numId = rs.getString(8);
					String compVend = rs.getString(9);
					String tipCard = rs.getString(10);

					out.append("<PARAMETER>");
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "CodEmp", getField(codigoEmpresa)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "Pedido", getField(codigoPedido)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "Valor", getField(valorPedido)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "tipPag", getField(tipoPagamento)));
					out.append(
							String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "sitPag", getField(situacaoPagamento)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "dtPag", getField(dataPagamento)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "codAut", getField(codAut)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "numId", getField(numId)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "compVend", getField(compVend)));
					out.append(String.format("<PARAM ID=\"%s\" VALUE=\"%s\" />", "tipCard", getField(tipCard)));
					out.append("</PARAMETER>");
				}
			} finally {
				if (ps != null)
					ps.close();
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
