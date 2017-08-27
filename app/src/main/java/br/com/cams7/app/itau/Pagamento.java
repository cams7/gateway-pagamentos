package br.com.cams7.app.itau;

import java.util.Date;

import br.com.cams7.app.model.entity.Pedido.TipoPagamento;
import br.com.cams7.app.model.entity.Pedido.SituacaoPagamento;

public class Pagamento {

	/**
	 * CodEmp: Código da Empresa (Código do Site)
	 * 
	 * Alfanumérico com exatas 26 posições (enviar o código todo em maiúsculas)
	 */
	@Parametro("CODEMP")
	private String codigoEmpresa;

	/**
	 * Pedido: Número do Pedido
	 * 
	 * Numérico com o máximo de 08 posições (99999999) ( * )
	 */
	@Parametro("PEDIDO")
	private Long numeroPedido;

	/**
	 * Valor: Valor Total do Pagamento
	 * 
	 * Números inteiros de até 8 posições (99999999,99). Os centavos deverão ser
	 * enviados com 2 casas decimais (não obrigatório), utilizando a vírgula como
	 * separador
	 */
	@Parametro("VALOR")
	private Double valorPagamento;

	/**
	 * TipPag: Tipo de pagamento escolhido pelo comprador
	 * 
	 * Numérico com 02 posições:
	 * 
	 * 00 para pagamento ainda não escolhido*
	 * 
	 * 01 para pagamento à vista (TEF e CDC)
	 * 
	 * 02 para boleto
	 * 
	 * 03 para cartão de crédito
	 * 
	 * OBS.: Este tipo de pagamento somente será exibido na consulta
	 */
	@Parametro("TIPPAG")
	private TipoPagamento tipoPagamento;

	/**
	 * SitPag: Situação de pagamento do pedido
	 * 
	 * Numérico com 02 posições:
	 * 
	 * 00 para pagamento efetuado
	 * 
	 * 01 para situação de pagamento não finalizada (tente novamente)
	 * 
	 * 02 para erro no processamento da consulta (tente novamente)
	 * 
	 * 03 para pagamento não localizado (consulta fora de prazo ou pedido não
	 * registrado no banco)
	 * 
	 * 04 para boleto emitido com sucesso
	 * 
	 * 05 para pagamento efetuado, aguardando compensação
	 * 
	 * 06 para pagamento não compensado
	 */
	@Parametro("SITPAG")
	private SituacaoPagamento situacaoPagamento;

	/**
	 * DtPag: Data do pagamento Numérico com
	 * 
	 * Numérico com 8 posições no formato "ddmmaaaa"
	 */
	@Parametro("DTPAG")
	private Date dataPagamento;

	/**
	 * CodAut: Número de autorização – preenchido somente quando pagamento efetuado
	 * com cartão de crédito
	 * 
	 * Alfanumérico com 6 posições
	 */
	@Parametro("CODAUT")
	private String codigoAutorizacao;

	/**
	 * NumId: NSU da transação – preenchido somente quando pagamento efetuado com
	 * cartão de crédito
	 * 
	 * Alfanumérico com 40 posições
	 */
	@Parametro("NUMID")
	private String nsuTransacao;

	/**
	 * CompVend: Número do comprovante de venda – preenchido somente quando
	 * pagamento efetuado com cartão de crédito, bandeira MasterCard®/Diners
	 * 
	 * Alfanumérico com 9 posições
	 */
	@Parametro("COMPVEND")
	private String comprovanteVenda;

	/**
	 * TipCart: Tipo de cartão de crédito escolhido pelo comprador
	 * 
	 */
	@Parametro("TIPCARD")
	private String tipoCartaoCredito;

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[codigoEmpresa = " + codigoEmpresa + ", numeroPedido = "
				+ numeroPedido + ", valorPagamento = " + valorPagamento + ", tipoPagamento = " + tipoPagamento
				+ ", situacaoPagamento = " + situacaoPagamento + ", dataPagamento = " + dataPagamento
				+ ", codigoAutorizacao = " + codigoAutorizacao + ", nsuTransacao = " + nsuTransacao
				+ ", comprovanteVenda = " + comprovanteVenda + ", tipoCartaoCredito = " + tipoCartaoCredito + "]";
	}

	public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public Long getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(Long numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(TipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public SituacaoPagamento getSituacaoPagamento() {
		return situacaoPagamento;
	}

	public void setSituacaoPagamento(SituacaoPagamento situacaoPagamento) {
		this.situacaoPagamento = situacaoPagamento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
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
