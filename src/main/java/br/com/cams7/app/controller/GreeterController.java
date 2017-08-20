package br.com.cams7.app.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.com.cams7.app.service.GreeterService;

@Named("greeter")
@SessionScoped
public class GreeterController implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected GreeterEJB client
	 */
	@EJB
	private GreeterService greeterService;

	/**
	 * Stores the response from the call to greeterEJB.sayHello(...)
	 */
	private String message;

	/**
	 * Invoke greeterEJB.sayHello(...) and store the message
	 * 
	 * @param name
	 *            The name of the person to be greeted
	 */
	public void setName(String name) {
		message = greeterService.sayHello(name);
	}

	/**
	 * Get the greeting message, customized with the name of the person to be
	 * greeted.
	 * 
	 * @return message. The greeting message.
	 */
	public String getMessage() {
		return message;
	}

}
