package scw.compensat.support;

import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import scw.compensat.CompensatException;
import scw.compensat.CompensatRegistry;
import scw.compensat.Compensator;
import scw.compensat.CompenstPolicy;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.retry.ExhaustedRetryException;
import scw.retry.RetryCallback;
import scw.retry.RetryContext;
import scw.retry.RetryOperations;
import scw.retry.support.RetryTemplate;

public class DefaultCompensatRegistry implements CompensatRegistry {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultCompensatRegistry.class);
	//守护进程自动退出
	private static Timer timer = new Timer(DefaultCompensatRegistry.class.getName(), true);
	private static ExecutorService executorService = Executors.newWorkStealingPool();
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				executorService.shutdownNow();
			}
		});
	}
	
	private final CompenstPolicy compenstPolicy;
	private final long period;
	private RetryOperations retryOperations = new RetryTemplate();

	/**
	 * @param compenstPolicy
	 * @param period 重试间隔时间
	 */
	public DefaultCompensatRegistry(CompenstPolicy compenstPolicy, int period) {
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
			throws CompensatException {
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
			} catch (ExhaustedRetryException e) {
				logger.error(e, "retry fail");
			} catch (Throwable e) {
				logger.error(e, "retry fail");
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
				CompenstThread compenstThread = new CompenstThread(compenstPolicy, group, period, TimeUnit.MILLISECONDS);
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
