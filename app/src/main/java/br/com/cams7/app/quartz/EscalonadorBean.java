package br.com.cams7.app.quartz;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cams7.app.model.entity.Pedido.FormaPagamento;

@Startup
@Singleton
public class EscalonadorBean {

	private Logger LOG = LoggerFactory.getLogger(EscalonadorBean.class);

	private Scheduler scheduler;

	@Inject
	private JobFactory jobFactory;

	@PostConstruct
	public void scheduleJobs() {

		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.setJobFactory(jobFactory);

			final String PAGAMENTO_NAO_VERIFICADO_JOB = "pagamento-nao-verificado-job";
			final String PAGAMENTO_NAO_ESCOLHIDO_JOB = "pagamento-nao-escolhido-job";
			final String PAGAMENTO_A_VISTA_JOB = "pagamento-a-vista-job";
			final String PAGAMENTO_CARTAO_CREDITO_JOB = "pagamento-cartao-credito-job";
			final String PAGAMENTO_BOLETO_JOB = "pagamento-boleto-job";

			// Carrega pedidos não verificados
			JobDetail carregaPedidosNaoVerificadosJob = JobBuilder.newJob(CarregaPedidosNaoVerificadosJob.class)
					.withIdentity(CarregaPedidosNaoVerificadosJob.CARREGA_PEDIDOS_NAO_VERIFICADOS).build();
			
			
			/**
			 * Field Name 	Mandatory 	Allowed Values 	 Allowed Special Characters
			 * Seconds 	    YES 	    0-59 	         , - * /
			 * Minutes 	    YES 	    0-59 	         , - * /
			 * Hours 	    YES 	    0-23 	         , - * /
			 * Day of month YES 	    1-31 	         , - * ? / L W
			 * Month 	    YES 	    1-12 or JAN-DEC  , - * /
			 * Day of week 	YES 	    1-7 or SUN-SAT 	 , - * ? / L #
			 * Year 	    NO 	        empty, 1970-2099 , - * /
			 */

			//Segundos, Minutos, Horas, Dia do Mês, Mês, Dia da Semana e Ano (Opcional)
			//Exemplo:
			//0 0 12 ? * WED A tarefa será executada todas às quartas-feiras às 12:00pm
			//0 0 8,12 * * * A tarefa será executada todos os dias, às 08:00am e 12:00pm
			Trigger carregaPedidosNaoVerificadosTrigger = TriggerBuilder.newTrigger()
					.withIdentity(getTriggerName(CarregaPedidosNaoVerificadosJob.CARREGA_PEDIDOS_NAO_VERIFICADOS))
					.startNow().withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?")).build();

			// Processa pagamento não verificado
			JobDetail pagamentoNaoVerificadoJob = JobBuilder.newJob(ProcessaPagamentoNaoVerificadoJob.class)
					.withIdentity(ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO).build();

			Trigger pagamentoNaoVerificadoTrigger = TriggerBuilder.newTrigger()
					.withIdentity(getTriggerName(ProcessaPagamentoNaoVerificadoJob.PAGAMENTO_NAO_VERIFICADO)).startNow()
					.withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();

//			// Processa pagamento não escolhido
//			JobDetail pagamentoNaoEscolhidoJob = JobBuilder.newJob(ProcessaPagamentoPendenteJob.class)
//					.withIdentity(JobKey.jobKey("job3", PAGAMENTO_NAO_ESCOLHIDO_JOB)).build();
//			pagamentoNaoEscolhidoJob.getJobDataMap().put(ProcessaPagamentoPendenteJob.FORMA_PAGAMENTO,
//					FormaPagamento.NAO_ESCOLHIDO);
//
//			Trigger pagamentoNaoEscolhidoTrigger = TriggerBuilder.newTrigger()
//					.withIdentity(TriggerKey.triggerKey("pagamento-nao-escolhido-trigger", PAGAMENTO_NAO_ESCOLHIDO_JOB))
//					.startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10)).build();
//
//			// Processa pagamento à vista
//			JobDetail pagamentoAVistaJob = JobBuilder.newJob(ProcessaPagamentoPendenteJob.class)
//					.withIdentity(JobKey.jobKey("job4", PAGAMENTO_A_VISTA_JOB)).build();
//			pagamentoAVistaJob.getJobDataMap().put(ProcessaPagamentoPendenteJob.FORMA_PAGAMENTO,
//					FormaPagamento.A_VISTA);
//
//			Trigger pagamentoAVistaTrigger = TriggerBuilder.newTrigger()
//					.withIdentity(TriggerKey.triggerKey("pagamento-a-vista-trigger", PAGAMENTO_A_VISTA_JOB)).startNow()
//					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30)).build();
//
//			// Processa pagamento no cartão de credito
//			JobDetail pagamentoCartaoCreditoJob = JobBuilder.newJob(ProcessaPagamentoPendenteJob.class)
//					.withIdentity(JobKey.jobKey("job5", PAGAMENTO_CARTAO_CREDITO_JOB)).build();
//			pagamentoCartaoCreditoJob.getJobDataMap().put(ProcessaPagamentoPendenteJob.FORMA_PAGAMENTO,
//					FormaPagamento.CARTAO_CREDITO);
//
//			Trigger pagamentoCartaoCreditoTrigger = TriggerBuilder.newTrigger()
//					.withIdentity(
//							TriggerKey.triggerKey("pagamento-cartao-credito-trigger", PAGAMENTO_CARTAO_CREDITO_JOB))
//					.startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();
//
//			// Processa pagamento no boleto bancário
//			JobDetail pagamentoBoletoJob = JobBuilder.newJob(ProcessaPagamentoPendenteJob.class)
//					.withIdentity(JobKey.jobKey("job6", PAGAMENTO_BOLETO_JOB)).build();
//			pagamentoBoletoJob.getJobDataMap().put(ProcessaPagamentoPendenteJob.FORMA_PAGAMENTO, FormaPagamento.BOLETO);
//
//			Trigger pagamentoBoletoTrigger = TriggerBuilder.newTrigger()
//					.withIdentity(TriggerKey.triggerKey("pagamento-boleto-trigger", PAGAMENTO_BOLETO_JOB)).startNow()
//					.withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();

			scheduler.start(); // starting a scheduler before scheduling jobs helped in getting rid of deadlock
								// on startup
			scheduler.scheduleJob(carregaPedidosNaoVerificadosJob, newHashSet(carregaPedidosNaoVerificadosTrigger),
					true);
			scheduler.scheduleJob(pagamentoNaoVerificadoJob, newHashSet(pagamentoNaoVerificadoTrigger), true);
//			scheduler.scheduleJob(pagamentoNaoEscolhidoJob, newHashSet(pagamentoNaoEscolhidoTrigger), true);
//			scheduler.scheduleJob(pagamentoAVistaJob, newHashSet(pagamentoAVistaTrigger), true);
//			scheduler.scheduleJob(pagamentoCartaoCreditoJob, newHashSet(pagamentoCartaoCreditoTrigger), true);
//			scheduler.scheduleJob(pagamentoBoletoJob, newHashSet(pagamentoBoletoTrigger), true);

			printJobsAndTriggers(scheduler);
		} catch (SchedulerException e) {
			LOG.error("Error while creating scheduler", e);
		}
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
