package br.com.cams7.app.quartz;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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

import br.com.cams7.app.model.entity.Order.PaymentMethod;

@Startup
@Singleton
public class SchedulerBean {

	private Logger LOG = LoggerFactory.getLogger(SchedulerBean.class);

	private Scheduler scheduler;

	@Inject
	private JobFactory jobFactory;

	@PostConstruct
	public void scheduleJobs() {

		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.setJobFactory(jobFactory);

			final String UNVERIFIED_PAYMENT_JOB = "pagamento-nao-verificado-job";
			final String PAYMENT_NOT_CHOSEN_JOB = "pagamento-nao-escolhido-job";
			final String CASH_PAYMENT_JOB = "pagamento-a-vista-job";
			final String CREDID_CARD_JOB = "cartao-credito-job";
			final String PAYMENT_SLIP_JOB = "boleto-bancario-job";

			// Processa pagamento não verificado
			JobDetail unverifiedPaymentJob = JobBuilder.newJob(ProcessUnverifiedPaymentJob.class)
					.withIdentity(JobKey.jobKey("job1", UNVERIFIED_PAYMENT_JOB)).build();

			Trigger unverifiedPaymentTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("pagamento-nao-verificado-trigger", UNVERIFIED_PAYMENT_JOB))
					.startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).build();

			// Processa pagamento não escolhido
			JobDetail notChosenJob = JobBuilder.newJob(ProcessPendingPaymentJob.class)
					.withIdentity(JobKey.jobKey("job2", PAYMENT_NOT_CHOSEN_JOB)).build();
			notChosenJob.getJobDataMap().put(ProcessPendingPaymentJob.PAYMENT_METHOD, PaymentMethod.PAYMENT_NOT_CHOSEN);

			Trigger notChosenTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("pagamento-nao-escolhido-trigger", PAYMENT_NOT_CHOSEN_JOB))
					.startNow().withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(10)).build();

			// Processa pagamento à vista
			JobDetail cashPaymentJob = JobBuilder.newJob(ProcessPendingPaymentJob.class)
					.withIdentity(JobKey.jobKey("job3", CASH_PAYMENT_JOB)).build();
			cashPaymentJob.getJobDataMap().put(ProcessPendingPaymentJob.PAYMENT_METHOD, PaymentMethod.CASH_PAYMENT);

			Trigger cashPaymentTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("pagamento-a-vista-trigger", CASH_PAYMENT_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30)).build();

			// Processa pagamento no cartão de credito
			JobDetail credidCardJob = JobBuilder.newJob(ProcessPendingPaymentJob.class)
					.withIdentity(JobKey.jobKey("job3", CREDID_CARD_JOB)).build();
			credidCardJob.getJobDataMap().put(ProcessPendingPaymentJob.PAYMENT_METHOD, PaymentMethod.CREDIT_CARD);

			Trigger credidCardTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("cartao-credito-trigger", CREDID_CARD_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();

			// Processa pagamento no boleto bancário
			JobDetail paymentSlipJob = JobBuilder.newJob(ProcessPendingPaymentJob.class)
					.withIdentity(JobKey.jobKey("job4", PAYMENT_SLIP_JOB)).build();
			paymentSlipJob.getJobDataMap().put(ProcessPendingPaymentJob.PAYMENT_METHOD, PaymentMethod.PAYMENT_SLIP);

			Trigger paymentSlipTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("boleto-bancario-trigger", PAYMENT_SLIP_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();

			scheduler.start(); // starting a scheduler before scheduling jobs helped in getting rid of deadlock
								// on startup
			scheduler.scheduleJob(unverifiedPaymentJob, newHashSet(unverifiedPaymentTrigger), true);
			scheduler.scheduleJob(notChosenJob, newHashSet(notChosenTrigger), true);
			scheduler.scheduleJob(cashPaymentJob, newHashSet(cashPaymentTrigger), true);
			scheduler.scheduleJob(credidCardJob, newHashSet(credidCardTrigger), true);
			scheduler.scheduleJob(paymentSlipJob, newHashSet(paymentSlipTrigger), true);

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
