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

import br.com.cams7.app.service.GreeterService;
import br.com.cams7.app.service.SimpleService;

@DisallowConcurrentExecution
@ExecuteInJTATransaction
public class SimpleJob implements Job {

	private static final Logger LOG = Logger.getLogger("MyJob");
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	@EJB
	private SimpleService simpleService;

	@EJB
	private GreeterService greeterService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			LOG.info(String.format("Trigger: %s, Fired at: %s, Instance: %s", context.getTrigger().getKey(),
					sdf.format(context.getFireTime()), context.getScheduler().getSchedulerInstanceId()));
		} catch (SchedulerException e) {
			// intentionally left blank
		}
		simpleService.doSomething();
		LOG.info(greeterService.sayHello(context.getTrigger().getKey().toString()));
	}
}
