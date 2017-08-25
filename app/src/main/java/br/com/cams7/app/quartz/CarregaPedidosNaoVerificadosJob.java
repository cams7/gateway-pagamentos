package br.com.cams7.app.quartz;

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
public class CarregaPedidosNaoVerificadosJob extends ProcessaPagamento implements Job {

	private static String JOB_NAME = "carrega-pedidos-nao-verificados";
	public static JobKey CARREGA_PEDIDOS_NAO_VERIFICADOS = JobKey.jobKey(JOB_NAME + "-job",
			ProcessaPagamentoNaoVerificadoJob.JOB_GROUP);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Scheduler scheduler = context.getScheduler();

			if (scheduler.checkExists(ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO)) {
				boolean paused = isPaused(scheduler, ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO);

				List<Long> pedidos = getPedidoRepository().buscaIdsPedidosNaoVerificados();
				System.out.println("Carrega: " + pedidos);

				if (pedidos.isEmpty()) {
					if (!paused)
						scheduler.pauseJob(ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO);
				} else {
					if (paused)
						scheduler.resumeJob(ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO);

					getPagamentos().add(ProcessaPagamentoNaoVerificadoJob.PAGAMENTOS_NAO_VERIFICADOS, pedidos);

				}
			}

			LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
					new Object[] { context.getTrigger().getKey(), SDF.format(context.getFireTime()),
							context.getScheduler().getSchedulerInstanceId() });
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

}
