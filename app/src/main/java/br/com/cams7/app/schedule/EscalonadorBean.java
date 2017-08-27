package br.com.cams7.app.schedule;

import static br.com.cams7.app.model.entity.Pedido.TipoPagamento.A_VISTA;
import static br.com.cams7.app.model.entity.Pedido.TipoPagamento.BOLETO;
import static br.com.cams7.app.model.entity.Pedido.TipoPagamento.CARTAO_CREDITO;
import static br.com.cams7.app.model.entity.Pedido.TipoPagamento.NAO_ESCOLHIDO;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PAGAMENTOS_NAO_ESCOLHIDOS;
import static br.com.cams7.app.model.entity.Tarefa.TarefaId.PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.schedule.jobs.CarregaPedidosNaoVerificadosJob.CARREGA_PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob.CARREGA_PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob.CARREGA_PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob.CARREGA_PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob.CARREGA_PAGAMENTOS_NAO_ESCOLHIDOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosNaoVerificadosJob.PROCESSA_PEDIDOS_NAO_VERIFICADOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.TIPO_PAGAMENTO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_A_VISTA;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_BOLETOS;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_CARTOES_CREDITO;
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cams7.app.model.TarefaRepository;
import br.com.cams7.app.model.entity.Pedido.TipoPagamento;
import br.com.cams7.app.model.entity.Tarefa;
import br.com.cams7.app.model.entity.Tarefa.TarefaId;
import br.com.cams7.app.schedule.jobs.CarregaPedidosNaoVerificadosJob;
import br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob;
import br.com.cams7.app.schedule.jobs.ProcessaPedidosNaoVerificadosJob;
import br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob;

@Startup
@Singleton
public class EscalonadorBean {

	private Logger LOG = LoggerFactory.getLogger(getClass().getName());

	private Scheduler scheduler;

	@Inject
	private JobFactory jobFactory;

	@EJB
	private TarefaRepository tarefaRepository;

	@PostConstruct
	public void scheduleJobs() {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.setJobFactory(jobFactory);

			// Pedidos não verificados
			registraPedidosNaoVerificados();

			// Pagamentos não escolhidos
			registraPagamentosNaoEscolhidos();

			// Pagamentos à vista
			registraPagamentosAVista();

			// Pagamentos realizados por cartões de crédito
			registraPagamentosCartoesCredito();

			// Pagamentos realizados por boletos bancário
			registraPagamentosBoletos();

			scheduler.start(); // starting a scheduler before scheduling jobs helped in getting rid of deadlock
								// on startup

			printJobsAndTriggers(scheduler);
		} catch (SchedulerException e) {
			LOG.error("Error while creating scheduler", e);
		}
	}

	/**
	 * Registra a tarefa que verifica os pedidos que ainda não foram processados
	 * 
	 * @throws SchedulerException
	 */
	private void registraPedidosNaoVerificados() throws SchedulerException {
		registraTarefa(PEDIDOS_NAO_VERIFICADOS, null, CarregaPedidosNaoVerificadosJob.class,
				CARREGA_PEDIDOS_NAO_VERIFICADOS, ProcessaPedidosNaoVerificadosJob.class,
				PROCESSA_PEDIDOS_NAO_VERIFICADOS);
	}

	/**
	 * Registra a tarefa que verifica os pagamentos não escolhidos retornados pela
	 * API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void registraPagamentosNaoEscolhidos() throws SchedulerException {
		registraPagamentos(PAGAMENTOS_NAO_ESCOLHIDOS, NAO_ESCOLHIDO, CARREGA_PAGAMENTOS_NAO_ESCOLHIDOS,
				PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS);
	}

	/**
	 * Registra a tarefa que verifica os pagamentos à vista retornados pela API do
	 * Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void registraPagamentosAVista() throws SchedulerException {
		registraPagamentos(PAGAMENTOS_A_VISTA, A_VISTA, CARREGA_PAGAMENTOS_A_VISTA, PROCESSA_PAGAMENTOS_A_VISTA);
	}

	/**
	 * Registra a tarefa que verifica os pagamentos realizados por cartões de
	 * crédito retornados pela API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void registraPagamentosCartoesCredito() throws SchedulerException {
		registraPagamentos(PAGAMENTOS_CARTOES_CREDITO, CARTAO_CREDITO, CARREGA_PAGAMENTOS_CARTOES_CREDITO,
				PROCESSA_PAGAMENTOS_CARTOES_CREDITO);
	}

	/**
	 * Registra a tarefa que verifica os pagamentos realizados por boletos bancário
	 * retornados pela API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void registraPagamentosBoletos() throws SchedulerException {
		registraPagamentos(PAGAMENTOS_BOLETOS, BOLETO, CARREGA_PAGAMENTOS_BOLETOS, PROCESSA_PAGAMENTOS_BOLETOS);
	}

	/**
	 * Registra a tarefa que verifica os pagamentos retornados pela API do Banco
	 * Itaú
	 * 
	 * @param rotina
	 *            Rotina de pagamentos
	 * @param tipoPagamento
	 *            Forma de pagamento
	 * @param carregaPagamentosKey
	 *            Carrega pagamentos
	 * @param processaPagamentosKey
	 *            Processa pagamentos
	 * 
	 * @throws SchedulerException
	 */
	private void registraPagamentos(final TarefaId rotina, final TipoPagamento tipoPagamento,
			final JobKey carregaPagamentosKey, final JobKey processaPagamentosKey) throws SchedulerException {

		registraTarefa(rotina, tipoPagamento, CarregaPedidosPendentesJob.class, carregaPagamentosKey,
				ProcessaPedidosPendentesJob.class, processaPagamentosKey);
	}

	/**
	 * Registra tarefa no escalonador
	 * 
	 * @param rotina
	 *            Rotina de pagamentos
	 * @param tipoPagamento
	 *            Forma de pagamento
	 * @param carregaPagamentosType
	 *            Job type
	 * @param carregaPagamentosKey
	 *            Carrega pagamentos
	 * @param processaPagamentosType
	 *            Job Type
	 * @param processaPagamentosKey
	 *            Processa pagamentos
	 * 
	 * @throws SchedulerException
	 */
	private void registraTarefa(final TarefaId rotina, final TipoPagamento tipoPagamento,
			final Class<? extends Job> carregaPagamentosType, final JobKey carregaPagamentosKey,
			final Class<? extends Job> processaPagamentosType, final JobKey processaPagamentosKey)
			throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(rotina);

		// Registra a tarefa de carregamento
		JobDetail carregaPagamentosJob = JobBuilder.newJob(carregaPagamentosType).withIdentity(carregaPagamentosKey)
				.build();
		if (tipoPagamento != null)
			carregaPagamentosJob.getJobDataMap().put(TIPO_PAGAMENTO, tipoPagamento);

		Trigger carregaPagamentosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(carregaPagamentosKey))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Registra a tarefa de processamento
		JobDetail processaPagamentosJob = JobBuilder.newJob(processaPagamentosType).withIdentity(processaPagamentosKey)
				.build();
		if (tipoPagamento != null)
			processaPagamentosJob.getJobDataMap().put(TIPO_PAGAMENTO, tipoPagamento);

		Trigger processaPagamentosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(processaPagamentosKey))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPagamentosJob, newHashSet(carregaPagamentosTrigger), true);
		scheduler.scheduleJob(processaPagamentosJob, newHashSet(processaPagamentosTrigger), true);
	}

	private Set<? extends Trigger> newHashSet(Trigger... trigger) {
		Set<Trigger> set = new HashSet<>();
		for (Trigger t : trigger) {
			set.add(t);
		}
		return set;
	}

	private void printJobsAndTriggers(Scheduler scheduler) throws SchedulerException {
		LOG.info("Quartz Scheduler: {}", scheduler.getSchedulerName());
		for (String group : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(group))) {
				LOG.info("Found job identified by {}", jobKey);
			}
		}
		for (String group : scheduler.getTriggerGroupNames()) {
			for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.<TriggerKey>groupEquals(group))) {
				LOG.info("Found trigger identified by {}", triggerKey);
			}
		}
	}

	private String getTriggerName(JobKey key) {
		return key.getName().replaceFirst("-job", "-trigger");
	}

	@PreDestroy
	public void stopJobs() {
		if (scheduler != null) {
			try {
				scheduler.shutdown(false);
			} catch (SchedulerException e) {
				LOG.error("Error while closing scheduler", e);
			}
		}
	}
}
