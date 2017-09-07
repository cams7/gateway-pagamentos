/**
 * 
 */
package br.com.cams7.app.controller;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * @author cesaram
 *
 */
public abstract class AppListProducer {

	@Inject
	private FacesContext facesContext;

	protected FacesContext getFacesContext() {
		return facesContext;
	}

}
