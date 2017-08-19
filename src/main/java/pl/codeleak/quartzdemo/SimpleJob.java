package pl.codeleak.quartzdemo;

import java.text.SimpleDateFormat;

import javax.ejb.EJB;

import org.jboss.as.quickstarts.ejbinwar.ejb.GreeterEJB;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.codeleak.quartzdemo.ejb.SimpleEjb;

@DisallowConcurrentExecution
@ExecuteInJTATransaction
public class SimpleJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger("MyJob");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @EJB
    private SimpleEjb simpleEjb;
    
    @EJB
    private GreeterEJB greeterEJB;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            LOG.info("Trigger: {}, Fired at: {}, Instance: {}",
                    context.getTrigger().getKey(),
                    sdf.format(context.getFireTime()),
                    context.getScheduler().getSchedulerInstanceId());
        } catch (SchedulerException e) {
            // intentionally left blank
        }
        simpleEjb.doSomething();
        LOG.info(greeterEJB.sayHello(context.getTrigger().getKey().toString()));
    }
}
