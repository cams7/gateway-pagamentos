package br.com.cams7.app.schedule.jobs;

import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_NAO_ESCOLHIDOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.JOB_GROUP;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.TIPO_PAGAMENTO;

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

import br.com.cams7.app.itau.Pagamento.TipoPagamento;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;

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
		TipoPagamento tipoPagamento = (TipoPagamento) data.get(TIPO_PAGAMENTO);

		List<Long> pedidos = getPedidoRepository().buscaPedidosPendentesPeloTipoPagamento(tipoPagamento);

		Scheduler scheduler = context.getScheduler();
		try {
			switch (tipoPagamento) {
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

	private void carregaPedidosPendentes(Scheduler scheduler, JobKey jobKey, TarefaId rotina, List<Long> pedidos)
			throws SchedulerException {
		pauseOrRestartJob(scheduler, jobKey, pedidos.isEmpty());

		getPedidosEncontrados().adiciona(rotina, pedidos);
	}

	private void exibeMensagem(String tipoPagamento, List<Long> pedidos) {
		if (!pedidos.isEmpty())
			LOG.log(Level.INFO, "Os \"{0}: {1}\" foi(ram) carregado(s).", new Object[] { tipoPagamento, pedidos });
		else
			LOG.log(Level.INFO, "Nenhum \"{0}\" foi carregado.", new Object[] { tipoPagamento });
	}

}
