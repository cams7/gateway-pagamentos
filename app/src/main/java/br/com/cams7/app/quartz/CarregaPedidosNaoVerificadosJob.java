package br.com.cams7.app.quartz;

import static br.com.cams7.app.quartz.ProcessaPedidosNaoVerificadosJob.PROCESSA_PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.quartz.ProcessaPedidosNaoVerificadosJob.PEDIDOS_NAO_VERIFICADOS;

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

	public static String JOB_GROUP = "pedidos-nao-verificados";
	private static String JOB_NAME = "carrega-" + JOB_GROUP;

	public static JobKey CARREGA_PEDIDOS_NAO_VERIFICADOS = JobKey.jobKey(getJobName(JOB_NAME), JOB_GROUP);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Scheduler scheduler = context.getScheduler();

			if (scheduler.checkExists(PROCESSA_PEDIDOS_NAO_VERIFICADOS)) {

				List<Long> pedidos = getPedidoRepository().buscaPedidosNaoVerificados();

				boolean empty = pedidos.isEmpty();

				pauseOrRestartJob(scheduler, PROCESSA_PEDIDOS_NAO_VERIFICADOS, empty);

				getPedidosEncontrados().adiciona(PEDIDOS_NAO_VERIFICADOS, pedidos);

				if (!empty)
					LOG.log(Level.INFO, "Os pedidos {0} foram carregados.", new Object[] { pedidos });
				else
					LOG.info("Nenhum pedido foi carregado.");

			}

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

}
