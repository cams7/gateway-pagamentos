/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.cams7.app.model;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.cams7.app.model.entity.Customer;

@Stateless
public class CustomerRepositoryImpl implements CustomerRepository {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<Customer> customerEventSrc;

	@Override
	public Customer findById(Long id) {
		return em.find(Customer.class, id);
	}

	@Override
	public List<Customer> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);

		Root<Customer> from = cq.from(Customer.class);
		cq.select(from);
		cq.orderBy(cb.asc(from.get("name")));

		TypedQuery<Customer> tq = em.createQuery(cq);
		List<Customer> customers = tq.getResultList();
		return customers;
	}

	@Override
	public void register(Customer customer) {
		log.info(String.format("Registering %s", customer.getName()));

		em.persist(customer);

		customerEventSrc.fire(customer);
	}
}
