package br.com.cams7.app.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import br.com.cams7.app.model.ClienteRepository;
import br.com.cams7.app.model.entity.Cliente;

@SuppressWarnings("serial")
@Model
public class ClienteController extends AppController {

	@EJB
	private ClienteRepository clienteRepository;

	private Cliente novoCliente;

	@Produces
	@Named
	public Cliente getNovoCliente() {
		return novoCliente;
	}

	@Override
	public void cadastra() {
		try {
			clienteRepository.cadastra(novoCliente);
			getFacesContext().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastrado!", "Cliente cadastrado com sucesso"));
			inicializaNovoCliente();
		} catch (Exception e) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getMensagemDeErro(e),
					"Ocorreu um erro ao tentar cadastrar o cliente"));
		}
	}

	@PostConstruct
	public void inicializaNovoCliente() {
		novoCliente = new Cliente();
	}

}
