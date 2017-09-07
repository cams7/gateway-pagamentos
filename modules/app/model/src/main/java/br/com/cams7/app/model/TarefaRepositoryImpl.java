/**
 * 
 */
package br.com.cams7.app.model;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.cams7.app.model.entity.Tarefa;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;

/**
 * @author César Magalhães
 *
 */
@Stateless
public class TarefaRepositoryImpl implements TarefaRepository {
	@Inject
	private EntityManager em;

	@Override
	public Tarefa buscaPeloId(TarefaId id) {
		return em.find(Tarefa.class, id);
	}

}
