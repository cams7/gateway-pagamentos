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
public class Order implements Serializable {

	@Id
	@SequenceGenerator(name = "orderSequence", sequenceName = "pedido_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderSequence")
	@Column(name = "pedido_id")
	private Long id;

	@NotNull
	@Column(name = "valor_pedido")
	private Float orderValue;

	@Column(name = "valor_pago")
	private Float valuePaid;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pedido")
	private Date orderDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_pagamento")
	private Date payDay;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_pagamento")
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "situacao_pagamento")
	private PaymentStatus paymentStatus;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_id")
	private Customer customer;

	public Order() {
		super();
	}

	public Order(Long id) {
		this();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(Float orderValue) {
		this.orderValue = orderValue;
	}

	public Float getValuePaid() {
		return valuePaid;
	}

	public void setValuePaid(Float valuePaid) {
		this.valuePaid = valuePaid;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getPayDay() {
		return payDay;
	}

	public void setPayDay(Date payDay) {
		this.payDay = payDay;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	// TIPPAG
	public enum PaymentMethod {
		// Manual Shopline do Itaú
		// Página 28
		// 2.5.3 Consulta Automática à Situação do Pagamento (sonda)
		PAYMENT_NOT_CHOSEN("00", "pagamento ainda não escolhido"), CASH_PAYMENT("01",
				"pagamento à vista (TEF e CDC)"), PAYMENT_SLIP("02",
						"boleto bancário"), CREDIT_CARD("03", "cartão de crédito");

		private String itauCode;
		private String description;

		private PaymentMethod(String itauCode, String description) {
			this.itauCode = itauCode;
			this.description = description;
		}

		protected String getItauCode() {
			return itauCode;
		}

		public String getDescription() {
			return description;
		}

		public static PaymentMethod getPaymentMethod(String itauCode) {
			if (itauCode == null)
				return null;

			for (PaymentMethod paymentMethod : values())
				if (paymentMethod.getItauCode().equals(itauCode))
					return paymentMethod;

			return null;
		}

	}

	// SITPAG
	public enum PaymentStatus {
		// Manual Shopline do Itaú
		// Página 28
		// 2.5.3 Consulta Automática à Situação do Pagamento (sonda)

		// On-line e real time
		PAYMENT_MADE("00", "pagamento efetuado"), UNFINISHED_PAYMENT("01",
				"situação de pagamento não finalizada (tente novamente)"), PROCESSING_ERROR("02",
						"erro no processamento da consulta (tente novamente)"), PAYMENT_NOT_FOUND("03",
								"pagamento não localizado (consulta fora de prazo ou pedido não registrado no banco)"), PAYMENT_SLIP_ISSUED(
										"04", "boleto emitido com sucesso"), AWAITING_COMPENSATION("05",
												"pagamento efetuado, aguardando compensação"), UNPAID_PAYMENT("06",
														"pagamento não compensado");

		private String itauCode;
		private String description;

		private PaymentStatus(String itauCode, String description) {
			this.itauCode = itauCode;
			this.description = description;
		}

		protected String getItauCode() {
			return itauCode;
		}

		public String getDescription() {
			return description;
		}

		public static PaymentStatus getOrderStatus(String itauCode) {
			if (itauCode == null)
				return null;

			for (PaymentStatus paymentStatus : values())
				if (paymentStatus.getItauCode().equals(itauCode))
					return paymentStatus;

			return null;
		}

	}

}
