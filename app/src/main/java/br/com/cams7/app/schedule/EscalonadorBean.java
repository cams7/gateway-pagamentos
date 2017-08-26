package br.com.cams7.app.schedule;

import static br.com.cams7.app.model.entity.Pedido.FormaPagamento.A_VISTA;
import static br.com.cams7.app.model.entity.Pedido.FormaPagamento.BOLETO;
import static br.com.cams7.app.model.entity.Pedido.FormaPagamento.CARTAO_CREDITO;
import static br.com.cams7.app.model.entity.Pedido.FormaPagamento.NAO_ESCOLHIDO;
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
import static br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob.FORMA_PAGAMENTO;
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
import br.com.cams7.app.model.entity.Tarefa;
import br.com.cams7.app.schedule.jobs.CarregaPedidosNaoVerificadosJob;
import br.com.cams7.app.schedule.jobs.CarregaPedidosPendentesJob;
import br.com.cams7.app.schedule.jobs.ProcessaPedidosNaoVerificadosJob;
import br.com.cams7.app.schedule.jobs.ProcessaPedidosPendentesJob;

@Startup
@Singleton
public class EscalonadorBean {

	private Logger LOG = LoggerFactory.getLogger(EscalonadorBean.class);

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
			pedidosNaoVerificados();

			// Pagamentos não escolhidos
			pagamentosNaoEscolhidos();

			// Pagamentos à vista
			pagamentosAVista();

			// Pagamentos realizados por cartões de crédito
			pagamentosCartoesCredito();

			// Pagamentos realizados por boletos bancário
			pagamentosBoletos();

			scheduler.start(); // starting a scheduler before scheduling jobs helped in getting rid of deadlock
								// on startup

			printJobsAndTriggers(scheduler);
		} catch (SchedulerException e) {
			LOG.error("Error while creating scheduler", e);
		}
	}

	/**
	 * Registra as rotinas que verifica os pedidos que ainda não foram processados
	 * 
	 * @throws SchedulerException
	 */
	private void pedidosNaoVerificados() throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(PEDIDOS_NAO_VERIFICADOS);
		
		// Carrega os pedidos não verificados
		JobDetail carregaPedidosNaoVerificadosJob = JobBuilder.newJob(CarregaPedidosNaoVerificadosJob.class)
				.withIdentity(CARREGA_PEDIDOS_NAO_VERIFICADOS).build();

		Trigger carregaPedidosNaoVerificadosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(CARREGA_PEDIDOS_NAO_VERIFICADOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Processa os pedidos não verificados
		JobDetail processaPedidosNaoVerificadosJob = JobBuilder.newJob(ProcessaPedidosNaoVerificadosJob.class)
				.withIdentity(PROCESSA_PEDIDOS_NAO_VERIFICADOS).build();

		Trigger processaPedidosNaoVerificadosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(PROCESSA_PEDIDOS_NAO_VERIFICADOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPedidosNaoVerificadosJob, newHashSet(carregaPedidosNaoVerificadosTrigger), true);
		scheduler.scheduleJob(processaPedidosNaoVerificadosJob, newHashSet(processaPedidosNaoVerificadosTrigger), true);
	}

	/**
	 * Registra as rotinas que verifica os pagamentos não escolhidos retornados pela
	 * API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void pagamentosNaoEscolhidos() throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(PAGAMENTOS_NAO_ESCOLHIDOS);
		
		// Carrega os pagamentos não escolhidos
		JobDetail carregaPagamentosNaoEscolhidosJob = JobBuilder.newJob(CarregaPedidosPendentesJob.class)
				.withIdentity(CARREGA_PAGAMENTOS_NAO_ESCOLHIDOS).build();
		carregaPagamentosNaoEscolhidosJob.getJobDataMap().put(FORMA_PAGAMENTO, NAO_ESCOLHIDO);

		Trigger carregaPagamentosNaoEscolhidosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(CARREGA_PAGAMENTOS_NAO_ESCOLHIDOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Processa os pagamentos não escolhidos
		JobDetail processaPagamentosNaoEscolhidosJob = JobBuilder.newJob(ProcessaPedidosPendentesJob.class)
				.withIdentity(PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS).build();
		processaPagamentosNaoEscolhidosJob.getJobDataMap().put(FORMA_PAGAMENTO, NAO_ESCOLHIDO);

		Trigger processaPagamentosNaoEscolhidosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(PROCESSA_PAGAMENTOS_NAO_ESCOLHIDOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPagamentosNaoEscolhidosJob, newHashSet(carregaPagamentosNaoEscolhidosTrigger),
				true);
		scheduler.scheduleJob(processaPagamentosNaoEscolhidosJob, newHashSet(processaPagamentosNaoEscolhidosTrigger),
				true);
	}

	/**
	 * Registra as rotinas que verifica os pagamentos à vista retornados pela API do
	 * Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void pagamentosAVista() throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(PAGAMENTOS_A_VISTA);
		
		// Carrega os pagamentos à vista
		JobDetail carregaPagamentosAVistaJob = JobBuilder.newJob(CarregaPedidosPendentesJob.class)
				.withIdentity(CARREGA_PAGAMENTOS_A_VISTA).build();
		carregaPagamentosAVistaJob.getJobDataMap().put(FORMA_PAGAMENTO, A_VISTA);

		Trigger carregaPagamentosAVistaTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(CARREGA_PAGAMENTOS_A_VISTA))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Processa os pagamentos à vista
		JobDetail processaPagamentosAVistaJob = JobBuilder.newJob(ProcessaPedidosPendentesJob.class)
				.withIdentity(PROCESSA_PAGAMENTOS_A_VISTA).build();
		processaPagamentosAVistaJob.getJobDataMap().put(FORMA_PAGAMENTO, A_VISTA);

		Trigger processaPagamentosAVistaTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(PROCESSA_PAGAMENTOS_A_VISTA))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPagamentosAVistaJob, newHashSet(carregaPagamentosAVistaTrigger), true);
		scheduler.scheduleJob(processaPagamentosAVistaJob, newHashSet(processaPagamentosAVistaTrigger), true);
	}

	/**
	 * Registra as rotinas que verifica os pagamentos realizados por cartões de
	 * crédito retornados pela API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void pagamentosCartoesCredito() throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(PAGAMENTOS_CARTOES_CREDITO);
		
		// Carrega os pagamentos realizados por cartões de crédito
		JobDetail carregaPagamentosCartoesCreditoJob = JobBuilder.newJob(CarregaPedidosPendentesJob.class)
				.withIdentity(CARREGA_PAGAMENTOS_CARTOES_CREDITO).build();
		carregaPagamentosCartoesCreditoJob.getJobDataMap().put(FORMA_PAGAMENTO, CARTAO_CREDITO);

		Trigger carregaPagamentosCartoesCreditoTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(CARREGA_PAGAMENTOS_CARTOES_CREDITO))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Processa os pagamentos realizados por cartões de crédito
		JobDetail processaPagamentosCartoesCreditoJob = JobBuilder.newJob(ProcessaPedidosPendentesJob.class)
				.withIdentity(PROCESSA_PAGAMENTOS_CARTOES_CREDITO).build();
		processaPagamentosCartoesCreditoJob.getJobDataMap().put(FORMA_PAGAMENTO, CARTAO_CREDITO);

		Trigger processaPagamentosCartoesCreditoTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(PROCESSA_PAGAMENTOS_CARTOES_CREDITO))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPagamentosCartoesCreditoJob, newHashSet(carregaPagamentosCartoesCreditoTrigger),
				true);
		scheduler.scheduleJob(processaPagamentosCartoesCreditoJob, newHashSet(processaPagamentosCartoesCreditoTrigger),
				true);
	}

	/**
	 * Registra as rotinas que verifica os pagamentos realizados por boletos
	 * bancário retornados pela API do Banco Itaú
	 * 
	 * @throws SchedulerException
	 */
	private void pagamentosBoletos() throws SchedulerException {
		Tarefa tarefa = tarefaRepository.buscaPeloId(PAGAMENTOS_BOLETOS);
		
		// Carrega os pagamentos realizados por boletos bancário
		JobDetail carregaPagamentosBoletosJob = JobBuilder.newJob(CarregaPedidosPendentesJob.class)
				.withIdentity(CARREGA_PAGAMENTOS_BOLETOS).build();
		carregaPagamentosBoletosJob.getJobDataMap().put(FORMA_PAGAMENTO, BOLETO);

		Trigger carregaPagamentosBoletosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(CARREGA_PAGAMENTOS_BOLETOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getCarregaCron())).build();

		// Processa os pagamentos realizados por boletos bancário
		JobDetail processaPagamentosBoletosJob = JobBuilder.newJob(ProcessaPedidosPendentesJob.class)
				.withIdentity(PROCESSA_PAGAMENTOS_BOLETOS).build();
		processaPagamentosBoletosJob.getJobDataMap().put(FORMA_PAGAMENTO, BOLETO);

		Trigger processaPagamentosBoletosTrigger = TriggerBuilder.newTrigger()
				.withIdentity(getTriggerName(PROCESSA_PAGAMENTOS_BOLETOS))
				.withSchedule(CronScheduleBuilder.cronSchedule(tarefa.getProcessaCron())).build();

		scheduler.scheduleJob(carregaPagamentosBoletosJob, newHashSet(carregaPagamentosBoletosTrigger), true);
		scheduler.scheduleJob(processaPagamentosBoletosJob, newHashSet(processaPagamentosBoletosTrigger), true);
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
