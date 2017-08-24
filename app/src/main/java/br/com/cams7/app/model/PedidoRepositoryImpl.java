/**
 * 
 */
package br.com.cams7.app.model;

import static br.com.cams7.app.model.entity.Pedido.SituacaoPagamento.ERRO_PROCESSAMENTO;
import static br.com.cams7.app.model.entity.Pedido.SituacaoPagamento.NAO_FINALIZADO;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 */
@Stateless
public class PedidoRepositoryImpl implements PedidoRepository {

	@Inject
	private EntityManager em;

	@Inject
	private Event<Pedido> pedidoEventSrc;

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.OrderRepository#findById(java.lang.Long)
	 */
	@Override
	public Pedido buscaPeloId(Long id) {
		return em.find(Pedido.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.OrderRepository#findAllByCustomer(java.lang.Long)
	 */
	@Override
	public List<Pedido> buscaTodosPeloCliente(Long clienteId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);

		Root<Pedido> from = cq.from(Pedido.class);
		cq.select(from);
		cq.where(cb.equal(from.get("cliente").get("id"), clienteId));
		cq.orderBy(cb.desc(from.get("dataPedido")));

		TypedQuery<Pedido> tq = em.createQuery(cq);
		List<Pedido> pedidos = tq.getResultList();
		return pedidos;
	}

	@Override
	public Pedido buscaPedidoNaoVerificado() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);

		Root<Pedido> from = cq.from(Pedido.class);
		cq.select(from);
		cq.where(cb.and(cb.isNull(from.get("formaPagamento")), cb.isNull(from.get("situacaoPagamento"))));
		cq.groupBy(from.<Number>get("id"));
		cq.having(cb.equal(from.get("id"), cb.min(from.<Number>get("id"))));

		try {
			TypedQuery<Pedido> tq = em.createQuery(cq);
			Pedido pedido = tq.getSingleResult();
			return pedido;
		} catch (NoResultException e) {

		}

		return null;
	}

	@Override
	public Pedido buscaPedidoPendente(FormaPagamento formaPagamento) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);

		Root<Pedido> from = cq.from(Pedido.class);
		cq.select(from);
		cq.where(cb.and(cb.equal(from.get("formaPagamento"), formaPagamento),
				cb.or(cb.equal(from.get("situacaoPagamento"), NAO_FINALIZADO),
						cb.equal(from.get("situacaoPagamento"), ERRO_PROCESSAMENTO))));
		cq.groupBy(from.<Number>get("id"));
		cq.having(cb.equal(from.get("id"), cb.min(from.<Number>get("id"))));

		try {
			TypedQuery<Pedido> tq = em.createQuery(cq);
			Pedido pedido = tq.getSingleResult();
			return pedido;
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
	public void cadastra(Pedido pedido) {
		em.persist(pedido);

		pedidoEventSrc.fire(pedido);
	}

	@Override
	public void atualiza(Pedido pedido) {
		em.merge(pedido);
	}

}
