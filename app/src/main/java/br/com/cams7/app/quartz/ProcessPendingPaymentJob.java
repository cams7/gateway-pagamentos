package br.com.cams7.app.quartz;

import java.util.logging.Level;

import org.quartz.DisallowConcurrentExecution;
//import org.quartz.ExecuteInJTATransaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import br.com.cams7.app.model.entity.Order;
import br.com.cams7.app.model.entity.Order.PaymentMethod;

/**
 * @author cesaram
 *
 *         Processa os pagamento à vista
 */
@DisallowConcurrentExecution
// @ExecuteInJTATransaction
public class ProcessPendingPaymentJob extends ProcessPayment implements Job {

	public static String PAYMENT_METHOD = "PaymentMethod";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap data = context.getJobDetail().getJobDataMap();
			PaymentMethod paymentMethod = (PaymentMethod) data.get(PAYMENT_METHOD);

			Order order = getOrderRepository().findPendingPayment(paymentMethod);
			if (order != null)
				processPayment(order);

			LOG.log(Level.INFO, "Trigger: {0}, Fired at: {1}, Instance: {2}",
					new Object[] { context.getTrigger().getKey(), SDF.format(context.getFireTime()),
							context.getScheduler().getSchedulerInstanceId() });
		} catch (SchedulerException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

}
