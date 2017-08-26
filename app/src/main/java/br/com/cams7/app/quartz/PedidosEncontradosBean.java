/**
 * 
 */
package br.com.cams7.app.quartz;

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
	private Map<String, List<Long>> parameters;

	@PostConstruct
	private void init() {
		parameters = new HashMap<>();
	}

	public void adiciona(String key, List<Long> ids) {
		parameters.put(key, ids);
	}

	public List<Long> getPedidos(String key) {
		return parameters.get(key);
	}
}
