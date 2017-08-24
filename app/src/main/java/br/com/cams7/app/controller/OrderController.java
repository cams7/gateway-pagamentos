/**
 * 
 */
package br.com.cams7.app.controller;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.cams7.app.model.OrderRepository;
import br.com.cams7.app.model.entity.Customer;
import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.PaymentMethod;
import br.com.cams7.app.model.entity.Order.PaymentStatus;

/**
 * @author cesaram
 *
 */
@SuppressWarnings("serial")
@Model
public class OrderController implements Serializable {

	@Inject
	private FacesContext facesContext;

	@EJB
	private OrderRepository orderRepository;

	private Order newOrder;

	private Long customerId;

	@Produces
	@Named
	public Order getNewOrder() {
		return newOrder;
	}

	public void register() {
		try {
			newOrder.setCustomer(new Customer(getCustomerId()));
			orderRepository.register(newOrder);
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastrado!", "Cadastrado com sucesso"));

			initNewOrder();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Ocorreu um erro"));
		}
	}

	@PostConstruct
	public void initNewOrder() {
		newOrder = new Order();
		newOrder.setOrderDate(new Date());
	}

	public PaymentMethod[] getPaymentMethods() {
		return PaymentMethod.values();
	}

	public PaymentStatus[] getOrderStatus() {
		return PaymentStatus.values();
	}

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

}
