/**
 * 
 */
package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.PaymentMethod;

/**
 * @author cesaram
 *
 */
public interface OrderRepository {
	Order findById(Long id);

	List<Order> findAllByCustomer(Long customerId);

	Order findByPaymentMethod(PaymentMethod paymentMethod);

	void register(Order order);
}
