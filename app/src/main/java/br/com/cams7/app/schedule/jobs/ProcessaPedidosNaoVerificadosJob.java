package br.com.cams7.app.schedule.jobs;

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

import br.com.cams7.app.model.entity.Pedido;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessaPedidosNaoVerificadosJob extends AppJob implements Job {

	public static String JOB_GROUP = "pedidos-nao-verificados";

	public static String PEDIDOS_NAO_VERIFICADOS = JOB_GROUP;

	public static JobKey PROCESSA_PEDIDOS_NAO_VERIFICADOS = JobKey.jobKey(getProcessaName(PEDIDOS_NAO_VERIFICADOS),
			JOB_GROUP);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Long> pedidos = getPedidosEncontrados().getPedidos(PEDIDOS_NAO_VERIFICADOS);

		exibeMensagem(pedidos);

		Scheduler scheduler = context.getScheduler();
		try {
			processaPedidosNaoVerificados(scheduler, pedidos);

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	private void processaPedidosNaoVerificados(Scheduler scheduler, List<Long> pedidos) throws SchedulerException {
		boolean empty = isEmpty(pedidos);

		pauseOrRestartJob(scheduler, PROCESSA_PEDIDOS_NAO_VERIFICADOS, empty);

		if (!empty) {
			Long pedidoId = pedidos.remove(0);
			Pedido pedido = getPedidoRepository().buscaPeloId(pedidoId);
			processaPedido(pedido);
		}
	}

	private void exibeMensagem(List<Long> pedidos) {
		if (!isEmpty(pedidos))
			LOG.log(Level.INFO, "Os \"pedidos não verificados: {0}\" serão processados.", new Object[] { pedidos });
		else
			LOG.info("Nenhum \"pedido não verificado\" será processado.");
	}

	private boolean isEmpty(List<Long> pedidos) {
		return pedidos == null || pedidos.isEmpty();
	}

}
