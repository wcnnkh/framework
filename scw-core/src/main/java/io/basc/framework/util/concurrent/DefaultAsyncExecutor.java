package io.basc.framework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultAsyncExecutor implements AsyncExecutor {
	private final ExecutorService executorService;

	public DefaultAsyncExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void execute(Runnable task) {
		executorService.execute(task);
	}

	public void execute(Runnable task, long startTimeout) {
		execute(task);
	}

	public Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}

	public <T> Future<T> submit(Callable<T> task) {
		return executorService.submit(task);
	}
}