package br.com.cams7.app.model;

import java.util.List;

import br.com.cams7.app.model.entity.Cliente;

public interface ClienteRepository {

	Cliente buscaPeloId(Long id);

	List<Cliente> buscaTodos();

	void cadastra(Cliente cliente);

}
