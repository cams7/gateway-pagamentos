package br.com.cams7.app.quartz;

import static br.com.cams7.app.quartz.CarregaPedidosNaoVerificadosJob.CARREGA_PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.quartz.CarregaPedidosNaoVerificadosJob.JOB_GROUP;

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

	private static String JOB_NAME = "processa-" + JOB_GROUP;

	public static JobKey PROCESSA_PEDIDOS_NAO_VERIFICADOS = JobKey.jobKey(getJobName(JOB_NAME), JOB_GROUP);

	public static String PEDIDOS_NAO_VERIFICADOS = "PedidosNaoVerificados";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Scheduler scheduler = context.getScheduler();

			if (scheduler.checkExists(CARREGA_PEDIDOS_NAO_VERIFICADOS)) {

				List<Long> pedidos = getPedidosEncontrados().getPedidos(PEDIDOS_NAO_VERIFICADOS);

				boolean empty = pedidos == null || pedidos.isEmpty();

				pauseOrRestartJob(scheduler, PROCESSA_PEDIDOS_NAO_VERIFICADOS, empty);

				if (!empty) {
					LOG.log(Level.INFO, "Os pedidos {0} serão processados.", new Object[] { pedidos });

					Long pedidoId = pedidos.remove(0);
					Pedido pedido = getPedidoRepository().buscaPeloId(pedidoId);
					processaPedido(pedido);
				} else
					LOG.info("Nenhum pedido será processado.");
			}

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

}
