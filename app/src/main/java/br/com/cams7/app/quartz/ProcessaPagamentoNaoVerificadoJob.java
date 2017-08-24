package br.com.cams7.app.quartz;

import java.util.logging.Level;

import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.entity.Pedido;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessaPagamentoNaoVerificadoJob extends ProcessaPagamento implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			Pedido pedido = getPedidoRepository().buscaPedidoNaoVerificado();
			if (pedido != null)
				processaPagamento(pedido);

			LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
					new Object[] { context.getTrigger().getKey(), SDF.format(context.getFireTime()),
							context.getScheduler().getSchedulerInstanceId() });
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

}
