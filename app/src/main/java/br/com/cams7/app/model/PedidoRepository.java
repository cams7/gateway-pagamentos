/**
 * 
 */
package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 */
public interface PedidoRepository {
	Pedido buscaPeloId(Long id);

	List<Pedido> buscaTodosPeloCliente(Long customerId);

	Pedido buscaPedidoNaoVerificado();

	Pedido buscaPedidoPendente(FormaPagamento formaPagamento);

	void cadastra(Pedido pedido);

	void atualiza(Pedido pedido);

	List<Long> buscaPedidosNaoVerificados();
}
