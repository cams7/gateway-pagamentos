/**
 * 
 */
package br.com.cams7.app.model.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;

/**
 * @author César Magalhães
 *
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

	@Id
	@SequenceGenerator(name = "pedidoSequence", sequenceName = "pedido_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedidoSequence")
	@Column(name = "pedido_id")
	private Long id;

	@NotNull
	@Column(name = "valor_pedido")
	private Double valorPedido;

	@Column(name = "valor_pago")
	private Double valorPago;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pedido")
	private Date dataPedido;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pagamento")
	private Date dataPagamento;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_pagamento")
	private TipoPagamento tipoPagamento;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "situacao_pagamento")
	private SituacaoPagamento situacaoPagamento;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	public Pedido() {
		super();
	}

	public Pedido(Long id) {
		this();
		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + id + ", valorPedido = " + getValorPedidoFormatado()
				+ ", valorPago = " + getValorPagoFormatado() + ", dataPedido = " + dataPedido + ", dataPagamento = "
				+ dataPagamento + ", tipoPagamento = " + tipoPagamento + ", situacaoPagamento = " + situacaoPagamento
				+ "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getValorPedido() {
		return valorPedido;
	}

	public void setValorPedido(Double valorPedido) {
		this.valorPedido = valorPedido;
	}

	public Double getValorPago() {
		return valorPago;
	}

	public void setValorPago(Double valorPago) {
		this.valorPago = valorPago;
	}

	public Date getDataPedido() {
		return dataPedido;
	}

	public void setDataPedido(Date dataPedido) {
		this.dataPedido = dataPedido;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getValorPedidoFormatado() {
		String valorFormatado = getValorFormatado(getValorPedido());
		return valorFormatado;
	}

	public String getValorPagoFormatado() {
		String valorFormatado = getValorFormatado(getValorPago());
		return valorFormatado;
	}

	public static String getValorFormatado(Double valor) {
		return new DecimalFormat("#.00").format(valor);
	}

}
