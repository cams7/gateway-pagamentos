/**
 * 
 */
package br.com.cams7.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author César Magalhães
 *
 */
public class PagamentoDAOImpl implements PagamentoDAO {

	private final DataSource dataSource;

	public PagamentoDAOImpl() {
		super();

		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/app");
		} catch (NamingException e) {
			throw new AppException(e);
		}
	}

	@Override
	public Pagamento buscaPeloPedido(String numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement(
						"select p.COD_PEDIDO, p.COD_EMP, p.VALOR, p.TIP_PAG, p.SIT_PAG, p.DT_PAG, p.COD_AUT, p.NUM_ID, p.COMP_VEND, p.TIP_CARD from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setString(1, numeroPedido);

				ResultSet rs = ps.executeQuery();

				Pagamento pagamento = new Pagamento();

				if (rs.next()) {
					pagamento.setNumeroPedido(rs.getString(1));
					pagamento.setCodigoEmpresa(rs.getString(2));
					pagamento.setValorPagamento(new DecimalFormat("#.00").format(rs.getDouble(3)));
					pagamento.setTipoPagamento(rs.getString(4));
					pagamento.setSituacaoPagamento(rs.getString(5));
					pagamento.setDataPagamento(rs.getString(6));
					pagamento.setCodigoAutorizacao(rs.getString(7));
					pagamento.setNsuTransacao(rs.getString(8));
					pagamento.setComprovanteVenda(rs.getString(9));
					pagamento.setTipoCartaoCredito(rs.getString(10));
				} else
					throw new AppException("Número do pedido inválido.", 404);

				if (rs.next())
					throw new AppException(
							String.format("Existe mais de um pagamento para o pedido (%s).", numeroPedido));

				return pagamento;

			} finally {
				if (ps != null)
					ps.close();
			}
		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
	}

	@Override
	public boolean existePagamento(String numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement("select count(p.COD_PEDIDO) from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setString(1, numeroPedido);

				ResultSet rs = ps.executeQuery();

				if (rs.next())
					return rs.getInt(1) > 0;
			} finally {
				if (ps != null)
					ps.close();
			}
		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
		return false;
	}

	@Override
	public void cadastra(Pagamento pagamento) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement(
						"insert into PEDIDO (COD_PEDIDO, COD_EMP, VALOR, TIP_PAG, SIT_PAG, DT_PAG) VALUES(?, ?, ?, ?, ?, ?)");

				ps.setString(1, pagamento.getNumeroPedido());
				ps.setString(2, pagamento.getCodigoEmpresa());
				ps.setDouble(3, Double.valueOf(pagamento.getValorPagamento().replaceFirst(",", ".")));
				ps.setString(4, pagamento.getTipoPagamento());
				ps.setString(5, pagamento.getSituacaoPagamento());
				ps.setString(6, pagamento.getDataPagamento());

				// execute insert SQL stetement
				ps.executeUpdate();

				System.out.println("O pagamento foi cadastrado com sucesso.");
			} finally {
				if (ps != null)
					ps.close();
			}
		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
	}

}
