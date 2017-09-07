package br.com.cams7.app;

import br.com.cams7.app.itau.Pagamento;
import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;

public interface PagamentoDAO {
	Pagamento buscaPeloPedido(Long numeroPedido);

	TipoPagamento buscaTipoPagamentoPeloPedido(Long numeroPedido);

	boolean existePagamento(Long numeroPedido);

	boolean seraProcessadoNovamente(Long numeroPedido);

	void cadastra(Pagamento pagamento);

	void atualiza(Long numeroPedido, SituacaoPagamento situacaoPagamento);

}
