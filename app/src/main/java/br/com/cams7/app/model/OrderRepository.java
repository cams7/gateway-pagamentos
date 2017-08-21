/**
 * 
 */
package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Order;

/**
 * @author cesaram
 *
 */
public interface OrderRepository {
	Order findById(Long id);

	List<Order> findAllByCustomer(Long customerId);

	void register(Order order);
}
