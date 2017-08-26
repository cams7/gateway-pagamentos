/**
 * 
 */
package br.com.cams7.app.model.entity;

import java.io.Serializable;
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
	private Float valorPedido;

	@Column(name = "valor_pago")
	private Float valorPago;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pedido")
	private Date dataPedido;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pagamento")
	private Date dataPagamento;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "forma_pagamento")
	private FormaPagamento formaPagamento;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getValorPedido() {
		return valorPedido;
	}

	public void setValorPedido(Float valorPedido) {
		this.valorPedido = valorPedido;
	}

	public Float getValorPago() {
		return valorPago;
	}

	public void setValorPago(Float valorPago) {
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

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
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

	// TIPPAG
	public enum FormaPagamento {
		// Manual Shopline do Itaú
		// Página 28
		// 2.5.3 Consulta Automática à Situação do Pagamento (sonda)
		NAO_ESCOLHIDO("00", "pagamento ainda não escolhido"),

		A_VISTA("01", "pagamento à vista (TEF e CDC)"),

		BOLETO("02", "boleto bancário"),

		CARTAO_CREDITO("03", "cartão de crédito");

		private String codigoItau;
		private String descricao;

		private FormaPagamento(String codigoItau, String descricao) {
			this.codigoItau = codigoItau;
			this.descricao = descricao;
		}

		public String getCodigoItau() {
			return codigoItau;
		}

		public String getDescricao() {
			return descricao;
		}

		public static FormaPagamento getFormaPagamento(String codigoItau) {
			if (codigoItau == null)
				return null;

			for (FormaPagamento formaPagamento : values())
				if (formaPagamento.getCodigoItau().equals(codigoItau))
					return formaPagamento;

			return null;
		}

	}

	// SITPAG
	public enum SituacaoPagamento {
		// Manual Shopline do Itaú
		// Página 28
		// 2.5.3 Consulta Automática à Situação do Pagamento (sonda)

		// On-line e real time
		EFETUADO("00", "pagamento efetuado"), NAO_FINALIZADO("01",
				"situação de pagamento não finalizada (tente novamente)"),

		ERRO_PROCESSAMENTO("02", "erro no processamento da consulta (tente novamente)"),

		NAO_LOCALIZADO("03", "pagamento não localizado (consulta fora de prazo ou pedido não registrado no banco)"),

		BOLETO_EMITIDO("04", "boleto emitido com sucesso"),

		AGUARDANDO_COMPENSACAO("05", "pagamento efetuado, aguardando compensação"),

		NAO_COMPENSADO("06", "pagamento não compensado");

		private String codigoItau;
		private String descricao;

		private SituacaoPagamento(String codigoItau, String descricao) {
			this.codigoItau = codigoItau;
			this.descricao = descricao;
		}

		public String getCodigoItau() {
			return codigoItau;
		}

		public String getDescricao() {
			return descricao;
		}

		public static SituacaoPagamento getSituacaoPagamento(String codigoItau) {
			if (codigoItau == null)
				return null;

			for (SituacaoPagamento situacaoPagamento : values())
				if (situacaoPagamento.getCodigoItau().equals(codigoItau))
					return situacaoPagamento;

			return null;
		}

	}

}
