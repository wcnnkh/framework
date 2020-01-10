package scw.util.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import scw.util.concurrent.ListenableFuture;
import scw.util.concurrent.ListenableFutureTask;

public class DefaultAsyncTaskExecutor implements AsyncListenableTaskExecutor {
	private ExecutorService executorService;

	public DefaultAsyncTaskExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void execute(Runnable task) {
		try {
			executorService.execute(task);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executorService + "] did not accept task: " + task, ex);
		}
	}

	public void execute(Runnable task, long startTimeout) {
		execute(task);
	}

	public Future<?> submit(Runnable task) {
		try {
			return executorService.submit(task);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executorService + "] did not accept task: " + task, ex);
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		try {
			return executorService.submit(task);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executorService + "] did not accept task: " + task, ex);
		}
	}

	public ListenableFuture<?> submitListenable(Runnable task) {
		try {
			ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
			executorService.execute(future);
			return future;
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executorService + "] did not accept task: " + task, ex);
		}
	}

	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		try {
			ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
			executorService.execute(future);
			return future;
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + executorService + "] did not accept task: " + task, ex);
		}
	}
}