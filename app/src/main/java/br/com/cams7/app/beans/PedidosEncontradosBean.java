/**
 * 
 */
package br.com.cams7.app.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import br.com.cams7.app.model.entity.Tarefa.TarefaId;

/**
 * @author cesaram
 *
 */
@ApplicationScoped
public class PedidosEncontradosBean {
	private Map<TarefaId, List<Long>> pedidos;

	@PostConstruct
	private void init() {
		pedidos = new HashMap<>();
	}

	public void adiciona(TarefaId rotina, List<Long> pedidos) {
		this.pedidos.put(rotina, pedidos);
	}

	public List<Long> getPedidos(TarefaId rotina) {
		return pedidos.get(rotina);
	}
}
