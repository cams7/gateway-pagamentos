package br.com.cams7.app;

public interface PagamentoDAO {
	Pagamento buscaPeloPedido(String numeroPedido);

	String buscaTipoPagamentoPeloPedido(String numeroPedido);

	boolean existePagamento(String numeroPedido);

	boolean seraProcessadoNovamente(String numeroPedido);

	void cadastra(Pagamento pagamento);

	void atualiza(String numeroPedido, String situacaoPagamento);

}
