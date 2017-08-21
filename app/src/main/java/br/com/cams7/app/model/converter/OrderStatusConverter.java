/**
 * 
 */
package br.com.cams7.app.model.converter;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.app.model.entity.Order.OrderStatus;

/**
 * @author cesaram
 *
 */
@FacesConverter("orderStatusConverter")
public class OrderStatusConverter extends EnumConverter {
	public OrderStatusConverter() {
		super(OrderStatus.class);
	}

}
