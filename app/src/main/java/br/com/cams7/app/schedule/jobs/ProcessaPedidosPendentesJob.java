package br.com.cams7.app.schedule.jobs;

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

import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessaPedidosPendentesJob extends AppJob implements Job {

	public static String JOB_GROUP = "pedidos-pendentes";

	public static String PAGAMENTOS_NAO_ESCOLHIDOS = "pagamentos-nao-escolhidos";
	public static String PAGAMENTOS_A_VISTA = "pagamentos-a-vista";
	public static String PAGAMENTOS_CARTOES_CREDITO = "pagamentos-cartoes-credito";
	public static String PAGAMENTOS_BOLETOS = "pagamento-boletos";

	public static JobKey PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS = JobKey.jobKey(getProcessaName(PAGAMENTOS_NAO_ESCOLHIDOS),
			JOB_GROUP);
	public static JobKey PROCESSA_PAGAMENTOS_A_VISTA = JobKey.jobKey(getProcessaName(PAGAMENTOS_A_VISTA), JOB_GROUP);
	public static JobKey PROCESSA_PAGAMENTOS_CARTOES_CREDITO = JobKey
			.jobKey(getProcessaName(PAGAMENTOS_CARTOES_CREDITO), JOB_GROUP);
	public static JobKey PROCESSA_PAGAMENTOS_BOLETOS = JobKey.jobKey(getProcessaName(PAGAMENTOS_BOLETOS), JOB_GROUP);

	public static String FORMA_PAGAMENTO = "FormaPagamento";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Scheduler scheduler = context.getScheduler();
		JobDataMap data = context.getJobDetail().getJobDataMap();
		FormaPagamento formaPagamento = (FormaPagamento) data.get(FORMA_PAGAMENTO);

		try {
			switch (formaPagamento) {
			case NAO_ESCOLHIDO: {
				List<Long> pedidos = getPedidosEncontrados().getPedidos(PAGAMENTOS_NAO_ESCOLHIDOS);

				exibeMensagem("pagamento(s) não escolhido(s)", pedidos);

				processaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS, pedidos);

				break;
			}
			case A_VISTA: {
				List<Long> pedidos = getPedidosEncontrados().getPedidos(PAGAMENTOS_A_VISTA);

				exibeMensagem("pagamento(s) à vista", pedidos);

				processaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_A_VISTA, pedidos);

				break;
			}
			case CARTAO_CREDITO: {
				List<Long> pedidos = getPedidosEncontrados().getPedidos(PAGAMENTOS_CARTOES_CREDITO);

				exibeMensagem("pagamento(s) realizado(s) por cartão(ões) de crédito", pedidos);

				processaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_CARTOES_CREDITO, pedidos);

				break;
			}
			case BOLETO: {
				List<Long> pedidos = getPedidosEncontrados().getPedidos(PAGAMENTOS_BOLETOS);

				exibeMensagem("pagamento(s) realizado(s) por boleto(s) bancário", pedidos);

				processaPedidosPendentes(scheduler, PROCESSA_PAGAMENTOS_BOLETOS, pedidos);

				break;
			}
			default:
				break;
			}

			showJobLog(context);
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	private void processaPedidosPendentes(Scheduler scheduler, JobKey jobKey, List<Long> pedidos)
			throws SchedulerException {
		boolean empty = isEmpty(pedidos);

		pauseOrRestartJob(scheduler, jobKey, empty);

		if (!empty) {
			Long pedidoId = pedidos.remove(0);
			Pedido pedido = getPedidoRepository().buscaPeloId(pedidoId);
			processaPedido(pedido);
		}
	}

	private void exibeMensagem(String tipoPagamento, List<Long> pedidos) {
		if (!isEmpty(pedidos))
			LOG.log(Level.INFO, "Os \"{0}: {1}\" será(ão) processado(s).", new Object[] { tipoPagamento, pedidos });
		else
			LOG.log(Level.INFO, "Nenhum \"{0}\" será processado.", new Object[] { tipoPagamento });
	}

	private boolean isEmpty(List<Long> pedidos) {
		return pedidos == null || pedidos.isEmpty();
	}

}
