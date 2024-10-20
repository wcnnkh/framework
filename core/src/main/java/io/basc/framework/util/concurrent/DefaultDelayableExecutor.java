package io.basc.framework.util.concurrent;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultDelayableExecutor implements DelayableExecutor {
	private static Logger logger = LogManager.getLogger(DefaultDelayableExecutor.class);
	private final ScheduledExecutorService scheduledExecutorService;

	public DefaultDelayableExecutor() {
		this(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
	}

	public DefaultDelayableExecutor(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit delayTimeUnit) {
		final String taskId = XUtils.getUUID();
		Callable<V> use = new Callable<V>() {

			@Override
			public V call() throws Exception {
				if (logger.isDebugEnabled()) {
					logger.debug("Thread[{}] execute schedule: {}", Thread.currentThread().getName(), this);
				}
				try {
					return callable.call();
				} catch (Throwable e) {
					logger.error(e, "Thread[{}] execute schedule fail: {}", Thread.currentThread().getName(), this);
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
			logger.debug("Thread[{}] schedule: {}", Thread.currentThread().getName(), use);
		}
		return scheduledExecutorService.schedule(use, delay, delayTimeUnit);
	}

}
