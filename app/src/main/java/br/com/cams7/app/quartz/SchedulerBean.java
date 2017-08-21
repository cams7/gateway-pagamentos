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

			final String DOWN_PAYMENT_JOB = "conta-corrente-job";
			final String CREDID_CARD_JOB = "cartao-credito-job";
			final String BANK_SLIP_JOB = "boleto-bancario-job";

			// Processa pagamento à vista
			JobDetail downPaymentJob = JobBuilder.newJob(DownPaymentJob.class)
					.withIdentity(JobKey.jobKey("job1", DOWN_PAYMENT_JOB)).build();

			Trigger downPaymentTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("conta-corrente-trigger", DOWN_PAYMENT_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(10)).build();

			// Processa pagamento no cartão de credito
			JobDetail credidCardJob = JobBuilder.newJob(CredidCardJob.class)
					.withIdentity(JobKey.jobKey("job2", CREDID_CARD_JOB)).build();

			Trigger credidCardTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("cartao-credito-trigger", CREDID_CARD_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30)).build();

			// Processa pagamento no boleto bancário
			JobDetail bankSlipJob = JobBuilder.newJob(BankSlipJob.class)
					.withIdentity(JobKey.jobKey("job3", BANK_SLIP_JOB)).build();

			Trigger bankSlipTrigger = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey("boleto-bancario-trigger", BANK_SLIP_JOB)).startNow()
					.withSchedule(SimpleScheduleBuilder.repeatMinutelyForever()).build();

			scheduler.start(); // starting a scheduler before scheduling jobs helped in getting rid of deadlock
								// on startup
			scheduler.scheduleJob(downPaymentJob, newHashSet(downPaymentTrigger), true);
			scheduler.scheduleJob(credidCardJob, newHashSet(credidCardTrigger), true);
			scheduler.scheduleJob(bankSlipJob, newHashSet(bankSlipTrigger), true);

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
