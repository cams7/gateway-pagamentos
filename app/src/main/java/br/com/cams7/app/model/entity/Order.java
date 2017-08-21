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
	@Column(name = "pedido_quantidade")
	private Short amount;

	@NotNull
	@Column(name = "pedido_custo")
	private Float cost;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pedido_data")
	private Date orderDate;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "pedido_forma_pagamento")
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "pedido_situacao")
	private OrderStatus orderStatus;

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

	public Short getAmount() {
		return amount;
	}

	public void setAmount(Short amount) {
		this.amount = amount;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public enum PaymentMethod {
		DOWN_PAYMENT("Conta corrente"), CREDIT_CARD("Cartão de Credido"), BANK_SLIP("Boleto bancário");

		private String description;

		private PaymentMethod(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public PaymentMethod getPaymentMethod() {
			return values()[ordinal()];
		}

	}

	public enum OrderStatus {
		PENDING("Pendente"), PAID_OUT("Pago"), CANCELED("Cancelado");

		private String description;

		private OrderStatus(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public OrderStatus getOrderStatus() {
			return values()[ordinal()];
		}

	}

}
