/**
 * 
 */
package br.com.cams7.app;

import static br.com.cams7.app.itau.ApiShopline.getDataPagamento;
import static br.com.cams7.app.itau.ApiShopline.getSituacaoPagamento;
import static br.com.cams7.app.itau.ApiShopline.getTipoPagamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import br.com.cams7.app.itau.Pagamento;
import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;
import br.com.cams7.app.util.AppException;

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
	public Pagamento buscaPeloPedido(Long numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement(
						"select p.COD_PEDIDO, p.COD_EMP, p.VALOR, p.TIP_PAG, p.SIT_PAG, p.DT_PAG, p.COD_AUT, p.NUM_ID, p.COMP_VEND, p.TIP_CARD from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setLong(1, numeroPedido);

				ResultSet rs = ps.executeQuery();

				Pagamento pagamento = new Pagamento();

				if (rs.next()) {
					pagamento.setNumeroPedido(rs.getLong(1));
					pagamento.setCodigoEmpresa(rs.getString(2));
					pagamento.setValorPagamento(rs.getDouble(3));
					pagamento.setTipoPagamento(getTipoPagamento(rs.getString(4)));
					pagamento.setSituacaoPagamento(getSituacaoPagamento(rs.getString(5)));
					pagamento.setDataPagamento(getDataPagamento(rs.getString(6)));
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
	public TipoPagamento buscaTipoPagamentoPeloPedido(Long numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("select p.TIP_PAG from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setLong(1, numeroPedido);

				ResultSet rs = ps.executeQuery();

				TipoPagamento tipoPagamento = null;

				if (rs.next())
					tipoPagamento = getTipoPagamento(rs.getString(1));

				if (rs.next())
					throw new AppException(
							String.format("Existe mais de um pagamento para o pedido (%s).", numeroPedido));

				return tipoPagamento;
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
	public boolean existePagamento(Long numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement("select count(p.COD_PEDIDO) from PEDIDO p where p.COD_PEDIDO = ?");
				ps.setLong(1, numeroPedido);

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
	public boolean seraProcessadoNovamente(Long numeroPedido) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement(
						"select count(p.COD_PEDIDO) from PEDIDO p where p.COD_PEDIDO = ? and (p.SIT_PAG = ? or p.SIT_PAG = ?)");
				ps.setLong(1, numeroPedido);
				ps.setString(2, getSituacaoPagamento(SituacaoPagamento.NAO_FINALIZADO));
				ps.setString(3, getSituacaoPagamento(SituacaoPagamento.ERRO_PROCESSAMENTO));

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

				ps.setLong(1, pagamento.getNumeroPedido());
				ps.setString(2, pagamento.getCodigoEmpresa());
				ps.setDouble(3, pagamento.getValorPagamento());
				ps.setString(4, getTipoPagamento(pagamento.getTipoPagamento()));
				ps.setString(5, getSituacaoPagamento(pagamento.getSituacaoPagamento()));
				ps.setString(6, getDataPagamento(pagamento.getDataPagamento()));

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

	@Override
	public void atualiza(Long numeroPedido, SituacaoPagamento situacaoPagamento) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement("update PEDIDO set SIT_PAG=? where COD_PEDIDO=?");

				ps.setString(1, situacaoPagamento.getCodigoItau());
				ps.setLong(2, numeroPedido);

				// execute insert SQL stetement
				ps.executeUpdate();

				System.out.println("O pagamento foi atualizado com sucesso.");
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
