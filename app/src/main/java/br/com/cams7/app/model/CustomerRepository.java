package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Customer;

public interface CustomerRepository {

	Customer findById(Long id);

	List<Customer> findAll();

	void register(Customer customer);

}
