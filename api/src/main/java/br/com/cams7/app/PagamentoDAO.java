package br.com.cams7.app;

public interface PagamentoDAO {
	Pagamento buscaPeloPedido(String numeroPedido);

	boolean existePagamento(String numeroPedido);

	void cadastra(Pagamento pagamento);

}
