package br.com.cams7.app.schedule.jobs;

import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosNaoVerificadosJob.JOB_GROUP;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosNaoVerificadosJob.PROCESSA_PEDIDOS_NAO_VERIFICADOS;

import java.util.List;
import java.util.logging.Level;

import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class CarregaPedidosNaoVerificadosJob extends AppJob implements Job {

	public static JobKey CARREGA_PEDIDOS_NAO_VERIFICADOS = JobKey.jobKey(getCarregaName(PEDIDOS_NAO_VERIFICADOS),
			JOB_GROUP);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		List<Long> pedidos = getPedidoRepository().buscaPedidosNaoVerificados();

		exibeMensagem(pedidos);

		Scheduler scheduler = context.getScheduler();
		try {
			carregaPedidosNaoVerificados(scheduler, pedidos);

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	private void carregaPedidosNaoVerificados(Scheduler scheduler, List<Long> pedidos) throws SchedulerException {
		pauseOrRestartJob(scheduler, PROCESSA_PEDIDOS_NAO_VERIFICADOS, pedidos.isEmpty());

		getPedidosEncontrados().adiciona(PEDIDOS_NAO_VERIFICADOS, pedidos);
	}

	private void exibeMensagem(List<Long> pedidos) {
		if (!pedidos.isEmpty())
			LOG.log(Level.INFO, "Os \"pedidos não verificados: {0}\" foram carregados.", new Object[] { pedidos });
		else
			LOG.info("Nenhum \"pedido não verificado\" foi carregado.");
	}

}
