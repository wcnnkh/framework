package scw.util.task;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import scw.core.Assert;
import scw.util.ConcurrencyThrottleSupport;
import scw.util.CustomizableThreadCreator;
import scw.util.concurrent.ListenableFuture;
import scw.util.concurrent.ListenableFutureTask;


public class SimpleAsyncTaskExecutor extends CustomizableThreadCreator
		implements AsyncListenableTaskExecutor, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Permit any number of concurrent invocations: that is, don't throttle concurrency.
	 * @see ConcurrencyThrottleSupport#UNBOUNDED_CONCURRENCY
	 */
	public static final int UNBOUNDED_CONCURRENCY = ConcurrencyThrottleSupport.UNBOUNDED_CONCURRENCY;

	/**
	 * Switch concurrency 'off': that is, don't allow any concurrent invocations.
	 * @see ConcurrencyThrottleSupport#NO_CONCURRENCY
	 */
	public static final int NO_CONCURRENCY = ConcurrencyThrottleSupport.NO_CONCURRENCY;


	/** Internal concurrency throttle used by this executor */
	private final ConcurrencyThrottleAdapter concurrencyThrottle = new ConcurrencyThrottleAdapter();

	private ThreadFactory threadFactory;

	private TaskDecorator taskDecorator;


	/**
	 * Create a new SimpleAsyncTaskExecutor with default thread name prefix.
	 */
	public SimpleAsyncTaskExecutor() {
		super();
	}

	/**
	 * Create a new SimpleAsyncTaskExecutor with the given thread name prefix.
	 * @param threadNamePrefix the prefix to use for the names of newly created threads
	 */
	public SimpleAsyncTaskExecutor(String threadNamePrefix) {
		super(threadNamePrefix);
	}

	/**
	 * Create a new SimpleAsyncTaskExecutor with the given external thread factory.
	 * @param threadFactory the factory to use for creating new Threads
	 */
	public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}


	/**
	 * Specify an external factory to use for creating new Threads,
	 * instead of relying on the local properties of this executor.
	 * <p>You may specify an inner ThreadFactory bean or also a ThreadFactory reference
	 * obtained from JNDI (on a Java EE 6 server) or some other lookup mechanism.
	 * @see #setThreadNamePrefix
	 * @see #setThreadPriority
	 */
	public void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}

	/**
	 * Return the external factory to use for creating new Threads, if any.
	 */
	public final ThreadFactory getThreadFactory() {
		return this.threadFactory;
	}

	/**
	 * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
	 * about to be executed.
	 * <p>Note that such a decorator is not necessarily being applied to the
	 * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
	 * execution callback (which may be a wrapper around the user-supplied task).
	 * <p>The primary use case is to set some execution context around the task's
	 * invocation, or to provide some monitoring/statistics for task execution.
	 */
	public final void setTaskDecorator(TaskDecorator taskDecorator) {
		this.taskDecorator = taskDecorator;
	}

	/**
	 * Set the maximum number of parallel accesses allowed.
	 * -1 indicates no concurrency limit at all.
	 * <p>In principle, this limit can be changed at runtime,
	 * although it is generally designed as a config time setting.
	 * NOTE: Do not switch between -1 and any concrete limit at runtime,
	 * as this will lead to inconsistent concurrency counts: A limit
	 * of -1 effectively turns off concurrency counting completely.
	 * @see #UNBOUNDED_CONCURRENCY
	 */
	public void setConcurrencyLimit(int concurrencyLimit) {
		this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
	}

	/**
	 * Return the maximum number of parallel accesses allowed.
	 */
	public final int getConcurrencyLimit() {
		return this.concurrencyThrottle.getConcurrencyLimit();
	}

	/**
	 * Return whether this throttle is currently active.
	 * @return {@code true} if the concurrency limit for this instance is active
	 * @see #getConcurrencyLimit()
	 * @see #setConcurrencyLimit
	 */
	public final boolean isThrottleActive() {
		return this.concurrencyThrottle.isThrottleActive();
	}


	/**
	 * Executes the given task, within a concurrency throttle
	 * if configured (through the superclass's settings).
	 * @see #doExecute(Runnable)
	 */
	public void execute(Runnable task) {
		execute(task, TIMEOUT_INDEFINITE);
	}

	/**
	 * Executes the given task, within a concurrency throttle
	 * if configured (through the superclass's settings).
	 * <p>Executes urgent tasks (with 'immediate' timeout) directly,
	 * bypassing the concurrency throttle (if active). All other
	 * tasks are subject to throttling.
	 * @see #TIMEOUT_IMMEDIATE
	 * @see #doExecute(Runnable)
	 */
	public void execute(Runnable task, long startTimeout) {
		Assert.notNull(task, "Runnable must not be null");
		Runnable taskToUse = (this.taskDecorator != null ? this.taskDecorator.decorate(task) : task);
		if (isThrottleActive() && startTimeout > TIMEOUT_IMMEDIATE) {
			this.concurrencyThrottle.beforeAccess();
			doExecute(new ConcurrencyThrottlingRunnable(taskToUse));
		}
		else {
			doExecute(taskToUse);
		}
	}

	public Future<?> submit(Runnable task) {
		FutureTask<Object> future = new FutureTask<Object>(task, null);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	public <T> Future<T> submit(Callable<T> task) {
		FutureTask<T> future = new FutureTask<T>(task);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	public ListenableFuture<?> submitListenable(Runnable task) {
		ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
		execute(future, TIMEOUT_INDEFINITE);
		return future;
	}

	/**
	 * Template method for the actual execution of a task.
	 * <p>The default implementation creates a new Thread and starts it.
	 * @param task the Runnable to execute
	 * @see #setThreadFactory
	 * @see #createThread
	 * @see java.lang.Thread#start()
	 */
	protected void doExecute(Runnable task) {
		Thread thread = (this.threadFactory != null ? this.threadFactory.newThread(task) : createThread(task));
		thread.start();
	}


	/**
	 * Subclass of the general ConcurrencyThrottleSupport class,
	 * making {@code beforeAccess()} and {@code afterAccess()}
	 * visible to the surrounding class.
	 */
	private static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport {
		private static final long serialVersionUID = 1L;

		@Override
		protected void beforeAccess() {
			super.beforeAccess();
		}

		@Override
		protected void afterAccess() {
			super.afterAccess();
		}
	}


	/**
	 * This Runnable calls {@code afterAccess()} after the
	 * target Runnable has finished its execution.
	 */
	private class ConcurrencyThrottlingRunnable implements Runnable {

		private final Runnable target;

		public ConcurrencyThrottlingRunnable(Runnable target) {
			this.target = target;
		}

		public void run() {
			try {
				this.target.run();
			}
			finally {
				concurrencyThrottle.afterAccess();
			}
		}
	}

}
