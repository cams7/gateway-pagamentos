/**
 * 
 */
package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.TipoPagamento;

/**
 * @author cesaram
 *
 */
public interface PedidoRepository {
	Pedido buscaPeloId(Long id);

	List<Pedido> buscaTodosPeloCliente(Long customerId);

	Pedido buscaPrimeiroPedidoNaoVerificado();

	Pedido buscaPrimeiroPedidoPendente(TipoPagamento tipoPagamento);

	void cadastra(Pedido pedido);

	void atualiza(Pedido pedido);

	List<Long> buscaPedidosNaoVerificados();

	List<Long> buscaPedidosPendentesPeloTipoPagamento(TipoPagamento tipoPagamento);
}
