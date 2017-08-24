package br.com.cams7.app.quartz;

import java.util.logging.Level;

import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessaPagamentoPendenteJob extends ProcessaPagamento implements Job {

	public static String FORMA_PAGAMENTO = "FormaPagamento";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			FormaPagamento formaPagamento = (FormaPagamento) data.get(FORMA_PAGAMENTO);

			Pedido pedido = getPedidoRepository().buscaPedidoPendente(formaPagamento);
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
