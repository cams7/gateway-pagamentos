package br.com.cams7.app.controller;
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import br.com.cams7.app.model.CustomerRepository;
import br.com.cams7.app.model.entity.Customer;

/**
 * @author Madhumita Sadhukhan
 */
@RequestScoped
public class CustomerListProducer {
	@EJB
	private CustomerRepository customerRepository;

	private List<Customer> customers;

	@Produces
	@Named
	public List<Customer> getCustomers() {
		return customers;
	}

	public void onCustomerListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Customer customer) {
		retrieveAllCustomers();
	}

	@PostConstruct
	public void retrieveAllCustomers() {
		customers = customerRepository.findAll();
	}
}
