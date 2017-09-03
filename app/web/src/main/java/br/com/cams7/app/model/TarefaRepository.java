/**
 * 
 */
package br.com.cams7.app.model;

import br.com.cams7.app.model.entity.Tarefa;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;

/**
 * @author César Magalhães
 *
 */
public interface TarefaRepository {

	Tarefa buscaPeloId(TarefaId id);

}
