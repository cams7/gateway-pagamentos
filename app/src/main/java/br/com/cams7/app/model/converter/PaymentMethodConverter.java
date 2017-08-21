/**
 * 
 */
package br.com.cams7.app.model.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.app.model.entity.Order.PaymentMethod;

/**
 * @author cesaram
 *
 */
@FacesConverter("paymentMethodConverter")
public class PaymentMethodConverter extends EnumConverter {
	public PaymentMethodConverter() {
		super(PaymentMethod.class);
	}

}
