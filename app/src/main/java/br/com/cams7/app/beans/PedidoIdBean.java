/**
 * 
 */
package br.com.cams7.app.beans;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author cesaram
 *
 */
@ApplicationScoped
public class PedidoIdBean {
	private List<Long> pedidos;

	public List<Long> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<Long> pedidos) {
		this.pedidos = pedidos;
	}

}
