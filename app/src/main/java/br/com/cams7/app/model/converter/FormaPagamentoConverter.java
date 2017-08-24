/**
 * 
 */
package br.com.cams7.app.model.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 */
@FacesConverter("formaPagamentoConverter")
public class FormaPagamentoConverter extends EnumConverter {
	public FormaPagamentoConverter() {
		super(FormaPagamento.class);
	}

}
