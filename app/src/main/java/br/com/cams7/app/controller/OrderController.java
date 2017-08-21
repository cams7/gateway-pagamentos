/**
 * 
 */
package br.com.cams7.app.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
//import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.cams7.app.model.OrderRepository;
import br.com.cams7.app.model.entity.Customer;
import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.OrderStatus;
import br.com.cams7.app.model.entity.Order.PaymentMethod;

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

	@Produces
	@Named
	public Order getNewOrder() {
		return newOrder;
	}

	public void register() {
		try {
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
		newOrder.setCustomer(new Customer(getCustomerId()));
		newOrder.setOrderStatus(OrderStatus.PENDING);
		newOrder.setOrderDate(new Date());
		int random = new Random().nextInt(PaymentMethod.values().length - 1);
		newOrder.setPaymentMethod(PaymentMethod.values()[random]);
	}

	public Long getCustomerId() {
		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		String param = params.get("c");
		Long customerId = Long.valueOf(param);
		return customerId;
	}

	public PaymentMethod[] getPaymentMethods() {
		return PaymentMethod.values();
	}

	public OrderStatus[] getOrderStatus() {
		return OrderStatus.values();
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

}
