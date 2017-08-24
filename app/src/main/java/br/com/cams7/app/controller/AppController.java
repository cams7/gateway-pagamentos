/**
 * 
 */
package br.com.cams7.app.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * @author cesaram
 *
 */
@SuppressWarnings("serial")
public abstract class AppController implements Serializable {

	@Inject
	private FacesContext facesContext;

	public abstract void cadastra();

	protected String getMensagemDeErro(Exception e) {
		// Default to general error message that registration failed.
		String mensagemDeErro = "Erro ao tentar cadastrar";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return mensagemDeErro;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			mensagemDeErro = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return mensagemDeErro;
	}

	protected FacesContext getFacesContext() {
		return facesContext;
	}

}
