/**
 * 
 */
package br.com.cams7.app.controller;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Cliente;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;
import br.com.cams7.app.model.entity.Pedido.SituacaoPagamento;

/**
 * @author cesaram
 *
 */
@SuppressWarnings("serial")
@Model
public class PedidoController extends AppController {

	@EJB
	private PedidoRepository pedidoRepository;

	private Pedido novoPedido;

	private Long clienteId;

	@Produces
	@Named
	public Pedido getNovoPedido() {
		return novoPedido;
	}

	
	@Override
	public void cadastra() {
		try {
			novoPedido.setCliente(new Cliente(getClienteId()));
			pedidoRepository.cadastra(novoPedido);
			getFacesContext().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Cadastrado!", "Pedido cadastrado com sucesso"));

			inicializaNovoPedido();
		} catch (Exception e) {
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getMensagemDeErro(e),
					"Ocorreu um erro ao tentar cadastrar o pedido"));
		}
	}

	@PostConstruct
	public void inicializaNovoPedido() {
		novoPedido = new Pedido();
		novoPedido.setDataPedido(new Date());
	}

	public FormaPagamento[] getFormaPagamento() {
		return FormaPagamento.values();
	}

	public SituacaoPagamento[] getSituacaoPagamento() {
		return SituacaoPagamento.values();
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

}
