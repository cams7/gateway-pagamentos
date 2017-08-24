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

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.cams7.app.model.entity.Cliente;

@Stateless
public class ClienteRepositoryImpl implements ClienteRepository {

	@Inject
	private EntityManager em;

	@Inject
	private Event<Cliente> clienteEventSrc;

	@Override
	public Cliente buscaPeloId(Long id) {
		return em.find(Cliente.class, id);
	}

	@Override
	public List<Cliente> buscaTodos() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);

		Root<Cliente> from = cq.from(Cliente.class);
		cq.select(from);
		cq.orderBy(cb.asc(from.get("nome")));

		TypedQuery<Cliente> tq = em.createQuery(cq);
		List<Cliente> clientes = tq.getResultList();
		return clientes;
	}

	@Override
	public void cadastra(Cliente cliente) {
		em.persist(cliente);

		clienteEventSrc.fire(cliente);
	}
}
