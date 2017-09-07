/**
 * 
 */
package br.com.cams7.app.controller;


import static br.com.cams7.app.itau.ApiShopline.getNumeroPedido;
import static br.com.cams7.app.itau.ApiShopline.getValorPagamento;
import static br.com.cams7.app.itau.ApiShopline.getDataPagamento;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import Itau.Itaucripto;
import br.com.cams7.app.itau.Pagamento.SituacaoPagamento;
import br.com.cams7.app.itau.Pagamento.TipoPagamento;
import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Cliente;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.util.AppConfig;

/**
 * @author cesaram
 *
 */
@SuppressWarnings("serial")
@Model
public class PedidoController extends AppController {

	private String urlShopline;

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

		urlShopline = AppConfig.getProperty("API_URL_SHOPLINE");
	}

	public TipoPagamento[] getTipoPagamento() {
		return TipoPagamento.values();
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

	public String getUrlShopline() {
		return urlShopline;
	}

	public String getDadosCriptografados(Pedido pedido) {
		final String CODIGO_EMPRESA = AppConfig.getProperty("API_CODIGO_EMPRESA");
		final String CHAVE_CRIPTOGRAFIA = AppConfig.getProperty("API_CHAVE_CRIPTOGRAFIA");
		final String URL_RETORNA = AppConfig.getProperty("APP_URL_RETORNO");
		
		final String NUMERO_PEDIDO = getNumeroPedido(pedido.getId());
		final String VALOR_PAGAMENTO = getValorPagamento(pedido.getValorPedido());
		final String DATA_VENCIMENTO = getDataPagamento(pedido.getDataPedido());

		final String CODIGO_INSCRICAO = "01";
		final String NUMERO_INSCRICAO = "88378658058";

		final String NOME_SACADO = "Arthur Benjamin Cavalcanti";

		final String ENDERECO_SACADO = "Rua Monsenhor Nicodemus dos Santos";
		final String BAIRRO_SACADO = "Piratininga";
		final String CEP_SACADO = "99999999";
		final String CIDADE_SACADO = "Niterói";
		final String ESTADO_SACADO = "RJ";

		final String OBSERVACAO = "3";
		final String OBS_ADICIONAL1 = "Essa é minha primeira observação adicional";
		final String OBS_ADICIONAL2 = "Essa é minha segunda observação adicional";
		final String OBS_ADICIONAL3 = "Essa é minha terceira observação adicional";
		
		Itaucripto itaucripto = new Itaucripto();
		String dadosCriptografados = itaucripto.geraDados(CODIGO_EMPRESA, NUMERO_PEDIDO, VALOR_PAGAMENTO, OBSERVACAO,
				CHAVE_CRIPTOGRAFIA, NOME_SACADO, CODIGO_INSCRICAO, NUMERO_INSCRICAO, ENDERECO_SACADO, BAIRRO_SACADO,
				CEP_SACADO, CIDADE_SACADO, ESTADO_SACADO, DATA_VENCIMENTO, URL_RETORNA, OBS_ADICIONAL1, OBS_ADICIONAL2,
				OBS_ADICIONAL3);
		
		return dadosCriptografados;
	}

}
