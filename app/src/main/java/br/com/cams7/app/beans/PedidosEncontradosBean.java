/**
 * 
 */
package br.com.cams7.app.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author cesaram
 *
 */
@ApplicationScoped
public class PedidosEncontradosBean {
	private Map<String, List<Long>> pedidos;

	@PostConstruct
	private void init() {
		pedidos = new HashMap<>();
	}

	public void adiciona(String tipo, List<Long> pedidos) {
		this.pedidos.put(tipo, pedidos);
	}

	public List<Long> getPedidos(String tipo) {
		return pedidos.get(tipo);
	}
}
