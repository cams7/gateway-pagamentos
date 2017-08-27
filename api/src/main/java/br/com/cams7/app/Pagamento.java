package br.com.cams7.app;

public class Pagamento {

	private String codigoEmpresa;
	private String numeroPedido;
	private String valorPagamento;
	private String tipoPagamento;
	private String situacaoPagamento;
	private String dataPagamento;
	private String codigoAutorizacao;
	private String nsuTransacao;
	private String comprovanteVenda;
	private String tipoCartaoCredito;

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public String getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(String valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public String getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public String getSituacaoPagamento() {
		return situacaoPagamento;
	}

	public void setSituacaoPagamento(String situacaoPagamento) {
		this.situacaoPagamento = situacaoPagamento;
	}

	public String getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(String dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getCodigoAutorizacao() {
		return codigoAutorizacao;
	}

	public void setCodigoAutorizacao(String codigoAutorizacao) {
		this.codigoAutorizacao = codigoAutorizacao;
	}

	public String getNsuTransacao() {
		return nsuTransacao;
	}

	public void setNsuTransacao(String nsuTransacao) {
		this.nsuTransacao = nsuTransacao;
	}

	public String getComprovanteVenda() {
		return comprovanteVenda;
	}

	public void setComprovanteVenda(String comprovanteVenda) {
		this.comprovanteVenda = comprovanteVenda;
	}

	public String getTipoCartaoCredito() {
		return tipoCartaoCredito;
	}

	public void setTipoCartaoCredito(String tipoCartaoCredito) {
		this.tipoCartaoCredito = tipoCartaoCredito;
	}

}
