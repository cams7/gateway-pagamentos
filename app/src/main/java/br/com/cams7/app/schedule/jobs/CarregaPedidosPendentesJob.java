package br.com.cams7.app.schedule.jobs;

import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.FORMA_PAGAMENTO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.JOB_GROUP;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PAGAMENTOS_NAO_ESCOLHIDOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS;

import java.util.List;
import java.util.logging.Level;

import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class CarregaPedidosPendentesJob extends AppJob implements Job {

	public static JobKey CARREGA_PAGAMENTOS_NAO_ESCOLHIDOS = JobKey.jobKey(getCarregaName(PAGAMENTOS_NAO_ESCOLHIDOS),
			JOB_GROUP);
	public static JobKey CARREGA_PAGAMENTOS_A_VISTA = JobKey.jobKey(getCarregaName(PAGAMENTOS_A_VISTA), JOB_GROUP);
	public static JobKey CARREGA_PAGAMENTOS_CARTOES_CREDITO = JobKey.jobKey(getCarregaName(PAGAMENTOS_CARTOES_CREDITO),
			JOB_GROUP);
	public static JobKey CARREGA_PAGAMENTOS_BOLETOS = JobKey.jobKey(getCarregaName(PAGAMENTOS_BOLETOS), JOB_GROUP);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		FormaPagamento formaPagamento = (FormaPagamento) data.get(FORMA_PAGAMENTO);

		List<Long> pedidos = getPedidoRepository().buscaPedidosPendentesPelaFormaPagamento(formaPagamento);

		Scheduler scheduler = context.getScheduler();
		try {
			switch (formaPagamento) {
			case NAO_ESCOLHIDO:
				exibeMensagem("pagamento(s) não escolhido(s)", pedidos);
				carregaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS, PAGAMENTOS_NAO_ESCOLHIDOS,
						pedidos);
				break;
			case A_VISTA:
				exibeMensagem("pagamento(s) à vista", pedidos);
				carregaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_A_VISTA, PAGAMENTOS_A_VISTA, pedidos);
				break;
			case CARTAO_CREDITO:
				exibeMensagem("pagamento(s) realizado(s) por cartão(ões) de crédito", pedidos);
				carregaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_CARTOES_CREDITO, PAGAMENTOS_CARTOES_CREDITO,
						pedidos);
				break;
			case BOLETO:
				exibeMensagem("pagamento(s) realizado(s) por boleto(s) bancário", pedidos);
				carregaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_BOLETOS, PAGAMENTOS_BOLETOS, pedidos);
				break;
			default:
				break;
			}

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	private void carregaPedidosPendentes(Scheduler scheduler, JobKey jobKey, String tipo, List<Long> pedidos)
			throws SchedulerException {
		pauseOrRestartJob(scheduler, jobKey, pedidos.isEmpty());

		getPedidosEncontrados().adiciona(tipo, pedidos);
	}

	private void exibeMensagem(String tipoPagamento, List<Long> pedidos) {
		if (!pedidos.isEmpty())
			LOG.log(Level.INFO, "Os \"{0}: {1}\" foi(ram) carregado(s).", new Object[] { tipoPagamento, pedidos });
		else
			LOG.log(Level.INFO, "Nenhum \"{0}\" foi carregado.", new Object[] { tipoPagamento });
	}

}
