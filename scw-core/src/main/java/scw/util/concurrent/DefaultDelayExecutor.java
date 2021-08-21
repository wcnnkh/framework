package scw.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.XUtils;

public class DefaultDelayExecutor implements DelayExecutor {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultDelayExecutor.class);
	private final ScheduledExecutorService scheduledExecutorService;

	public DefaultDelayExecutor() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime()
				.availableProcessors()));
	}

	public DefaultDelayExecutor(
			ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay,
			TimeUnit delayTimeUnit) {
		final String taskId = XUtils.getUUID();
		Callable<V> use = new Callable<V>() {

			@Override
			public V call() throws Exception {
				try {
					return callable.call();
				} catch (Throwable e) {
					logger.error(e, "Schedule fail: {}", this);
					if (e instanceof Exception) {
						throw (Exception) e;
					}
					throw new ExecutionException(e);
				}
			}

			@Override
			public String toString() {
				return taskId + "[" + callable.toString() + "]";
			}
		};

		if (logger.isDebugEnabled()) {
			logger.debug("Schedule: {}", use);
		}
		return scheduledExecutorService.schedule(use, delay, delayTimeUnit);
	}

}
