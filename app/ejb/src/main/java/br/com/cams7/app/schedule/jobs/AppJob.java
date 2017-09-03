/**
 * 
 */
package br.com.cams7.app.schedule.jobs;

import static br.com.cams7.app.itau.ApiShopline.getPagamentos;
import static br.com.cams7.app.itau.ApiShopline.getRespostaItau;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.w3c.dom.Document;

import br.com.cams7.app.beans.PedidosEncontradosBean;
import br.com.cams7.app.itau.Pagamento;
import br.com.cams7.app.model.PedidoRepository;
import br.com.cams7.app.model.entity.Pedido;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;
import br.com.cams7.app.util.AppException;

/**
 * @author cesaram
 *
 */
public abstract class AppJob {

	protected final Logger LOG = Logger.getLogger(getClass().getSimpleName());

	@Inject
	private PedidosEncontradosBean pedidosEncontrados;

	@EJB
	private PedidoRepository pedidoRepository;

	protected void processaPedido(Pedido pedido) {

		String codigoEmpresa = "J0000560680005480000000013";

		try {
			Document document = getRespostaItau("http://localhost:8090/gateway-pagamentos-api/consulta", codigoEmpresa,
					pedido.getId());
			List<Pagamento> pagamentos = getPagamentos(document);

			for (Pagamento pagamento : pagamentos) {
				if (!pagamento.getNumeroPedido().equals(pedido.getId()))
					throw new AppException(String.format(
							"O número do pedido (%s), retornado pelo Banco Itaú, não é o mesmo número (%s) que está cadastrado na base de dados",
							pagamento.getNumeroPedido(), pedido.getId()));

				pedido.setValorPago(pagamento.getValorPagamento());
				pedido.setTipoPagamento(pagamento.getTipoPagamento());
				pedido.setSituacaoPagamento(pagamento.getSituacaoPagamento());
				pedido.setDataPagamento(pagamento.getDataPagamento());

				getPedidoRepository().atualiza(pedido);
			}

			if (!pagamentos.isEmpty())
				LOG.log(Level.INFO, "O pedido ({0}) foi processado...", new Object[] { pedido.getId() });
		} catch (AppException e) {
			LOG.log(Level.WARNING, "Pedido ({0}): {1}", new Object[] { pedido.getId(), e.getMessage() });
		}

	}

	protected void pauseOrRestartJob(Scheduler scheduler, JobKey jobKey, boolean pause) throws SchedulerException {
		boolean paused = isPaused(scheduler, jobKey);

		if (pause) {
			if (!paused)
				scheduler.pauseJob(jobKey);
		} else if (paused)
			scheduler.resumeJob(jobKey);

	}

	private boolean isPaused(Scheduler scheduler, JobKey jobKey) throws SchedulerException {
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
		for (Trigger trigger : triggers) {
			TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

			if (TriggerState.PAUSED.equals(triggerState))
				return true;
		}

		return false;
	}

	protected void showJobLog(JobExecutionContext context) throws SchedulerException {
		// LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
		// new Object[] { context.getTrigger().getKey(),
		// new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(context.getFireTime()),
		// context.getScheduler().getSchedulerInstanceId() });
	}

	protected static String getCarregaName(TarefaId rotina) {
		return getJobName("carrega-" + rotina.getCodigo());
	}

	protected static String getProcessaName(TarefaId rotina) {
		return getJobName("processa-" + rotina.getCodigo());
	}

	private static String getJobName(String name) {
		return name + "-job";
	}

	protected PedidoRepository getPedidoRepository() {
		return pedidoRepository;
	}

	protected PedidosEncontradosBean getPedidosEncontrados() {
		return pedidosEncontrados;
	}

}
