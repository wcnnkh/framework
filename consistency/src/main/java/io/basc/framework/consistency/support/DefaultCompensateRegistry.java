package io.basc.framework.consistency.support;

import io.basc.framework.consistency.CompensateException;
import io.basc.framework.consistency.CompensatePolicy;
import io.basc.framework.consistency.CompensateRegistry;
import io.basc.framework.consistency.Compensator;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.retry.RetryCallback;
import io.basc.framework.retry.RetryContext;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;

import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultCompensateRegistry implements CompensateRegistry {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultCompensateRegistry.class);
	//守护进程自动退出
	private static Timer timer = new Timer(DefaultCompensateRegistry.class.getName(), true);
	private static ExecutorService executorService = Executors.newWorkStealingPool();
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				executorService.shutdownNow();
			}
		});
	}
	
	private final CompensatePolicy compenstPolicy;
	private final long period;
	private RetryOperations retryOperations = new RetryTemplate();
	
	public DefaultCompensateRegistry(CompensatePolicy compenstPolicy) {
		this(compenstPolicy, 1);
	}

	/**
	 * @param compenstPolicy
	 * @param period 重试间隔时间(分钟)
	 */
	public DefaultCompensateRegistry(CompensatePolicy compenstPolicy, int period) {
		this.compenstPolicy = compenstPolicy;
		this.period = TimeUnit.MINUTES.toMillis(period);
		timer.schedule(new CompenstTimerTask(), this.period, this.period);
	}
	
	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		this.retryOperations = retryOperations;
	}

	@Override
	public Compensator register(String group, String id, Runnable runnable)
			throws CompensateException {
		if (compenstPolicy.add(group, id, runnable)) {
			return new DefaultCompensator(group, id, new RetryRunnable(runnable), compenstPolicy);
		} else {
			return new DefaultCompensator(group, id, new RegisterRunnable(
					group, id, runnable), compenstPolicy);
		}
	}
	
	private class RetryRunnable implements Runnable{
		private final Runnable runnable;
		
		public RetryRunnable(Runnable runnable) {
			this.runnable = runnable;
		}
		
		@Override
		public void run() {
			try {
				retryOperations.execute(new RetryCallback<Void, Throwable>() {

					@Override
					public Void doWithRetry(RetryContext context) throws Throwable {
						runnable.run();
						return null;
					}
				});
			} catch (Throwable e) {
				throw new CompensateException(e);
			}
		}
	}

	@Override
	public Compensator getCompensator(String group, String id) {
		return new DefaultCompensator(group, id, new GetRunnable(group, id),
				compenstPolicy);
	}

	private final class CompenstTimerTask extends TimerTask{
		
		@Override
		public void run() {
			Enumeration<String> groups = compenstPolicy.getUnfinishedGroups();
			while(groups.hasMoreElements()){
				String group = groups.nextElement();
				if(group == null){
					continue;
				}
				
				logger.debug("create compenst [{}] thread", group);
				CompensateThread compenstThread = new CompensateThread(compenstPolicy, group, period, TimeUnit.MILLISECONDS);
				executorService.submit(compenstThread);
			}
		}
	}

	private final class GetRunnable implements Runnable {
		private final String group;
		private final String id;

		public GetRunnable(String group, String id) {
			this.group = group;
			this.id = id;
		}

		@Override
		public void run() {
			Runnable runnable = compenstPolicy.get(group, id);
			if (runnable != null) {
				new RetryRunnable(runnable).run();
			}
		}
	}

	private final class RegisterRunnable implements Runnable {
		private final String group;
		private final String id;
		private final Runnable runnable;

		public RegisterRunnable(String group, String id, Runnable runnable) {
			this.runnable = runnable;
			this.group = group;
			this.id = id;
		}

		@Override
		public void run() {
			Runnable runnable = compenstPolicy.get(group, id);
			if (runnable == null) {
				runnable = this.runnable;
			}
			new RetryRunnable(runnable).run();
		}
	}
}
