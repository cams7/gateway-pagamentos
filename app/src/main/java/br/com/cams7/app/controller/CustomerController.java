package br.com.cams7.app.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.cams7.app.model.CustomerRepository;
import br.com.cams7.app.model.entity.Customer;

@Model
public class CustomerController {

	@Inject
	private FacesContext facesContext;

	@EJB
	private CustomerRepository customerRepository;

	private Customer newCustomer;

	@Produces
	@Named
	public Customer getNewCustomer() {
		return newCustomer;
	}

	public void register() {
		try {
			customerRepository.register(newCustomer);
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastrado!", "Cadastrado com sucesso"));
			initNewCustomer();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Ocorreu um erro"));
		}
	}

	@PostConstruct
	public void initNewCustomer() {
		newCustomer = new Customer();
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
