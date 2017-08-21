package br.com.cams7.app.quartz;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.CustomerRepository;
import br.com.cams7.app.model.entity.Customer;

@DisallowConcurrentExecution
@ExecuteInJTATransaction
public class SimpleJob implements Job {

	private static final Logger LOG = Logger.getLogger("MyJob");
	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

	@EJB
	private CustomerRepository customerRepository;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			LOG.info(String.format("Trigger: %s, Fired at: %s, Instance: %s", context.getTrigger().getKey(),
					SDF.format(context.getFireTime()), context.getScheduler().getSchedulerInstanceId()));
		} catch (SchedulerException e) {
			// intentionally left blank
		}
		Customer customer = customerRepository.findById(1L);
		LOG.info(String.format("O cliente %s foi consultado", customer.getEmail()));
	}
}
