/**
 * 
 */
package br.com.cams7.app.model;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.PaymentMethod;

/**
 * @author cesaram
 *
 */
@Stateless
public class OrderRepositoryImpl implements OrderRepository {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<Order> orderEventSrc;

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.OrderRepository#findById(java.lang.Long)
	 */
	@Override
	public Order findById(Long id) {
		return em.find(Order.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.OrderRepository#findAllByCustomer(java.lang.Long)
	 */
	@Override
	public List<Order> findAllByCustomer(Long customerId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);

		Root<Order> from = cq.from(Order.class);
		cq.select(from);
		cq.where(cb.equal(from.get("customer").get("id"), customerId));
		cq.orderBy(cb.desc(from.get("orderDate")));

		TypedQuery<Order> tq = em.createQuery(cq);
		List<Order> orders = tq.getResultList();
		return orders;
	}

	@Override
	public Order findByPaymentMethod(PaymentMethod paymentMethod) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Order> cq = cb.createQuery(Order.class);

		Root<Order> from = cq.from(Order.class);
		cq.select(from);
		cq.where(cb.equal(from.get("paymentMethod"), paymentMethod));
		cq.groupBy(from.<Number>get("id"));
		cq.having(cb.equal(from.get("id"), cb.min(from.<Number>get("id"))));

		try {
			TypedQuery<Order> tq = em.createQuery(cq);
			Order order = tq.getSingleResult();
			return order;
		} catch (NoResultException e) {

		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.model.OrderRepository#register(br.com.cams7.app.model.entity
	 * .Order)
	 */
	@Override
	public void register(Order order) {
		log.info(String.format("Registering %s",
				new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getOrderDate())));

		em.persist(order);

		orderEventSrc.fire(order);
	}

}
