/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.soeasy.framework.util.concurrent.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.concurrent.AsyncExecutor;
import run.soeasy.framework.util.concurrent.ListenableFuture;
import run.soeasy.framework.util.concurrent.ListenableFutureTask;
import run.soeasy.framework.util.concurrent.TaskDecorator;

/**
 * @see java.util.concurrent.Executor
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 */
public class TaskExecutorAdapter implements AsyncExecutor {

	private final Executor concurrentExecutor;

	private TaskDecorator taskDecorator;

	/**
	 * Create a new TaskExecutorAdapter, using the given JDK concurrent executor.
	 * 
	 * @param concurrentExecutor the JDK concurrent executor to delegate to
	 */
	public TaskExecutorAdapter(Executor concurrentExecutor) {
		Assert.notNull(concurrentExecutor, "Executor must not be null");
		this.concurrentExecutor = concurrentExecutor;
	}

	/**
	 * Specify a custom {@link TaskDecorator} to be applied to any {@link Runnable}
	 * about to be executed.
	 * <p>
	 * Note that such a decorator is not necessarily being applied to the
	 * user-supplied {@code Runnable}/{@code Callable} but rather to the actual
	 * execution callback (which may be a wrapper around the user-supplied task).
	 * <p>
	 * The primary use case is to set some execution context around the task's
	 * invocation, or to provide some monitoring/statistics for task execution.
	 * 
	 */
	public final void setTaskDecorator(TaskDecorator taskDecorator) {
		this.taskDecorator = taskDecorator;
	}

	/**
	 * Delegates to the specified JDK concurrent executor.
	 * 
	 * @see java.util.concurrent.Executor#execute(Runnable)
	 */
	public void execute(Runnable task) {
		doExecute(this.concurrentExecutor, this.taskDecorator, task);
	}

	public void execute(Runnable task, long startTimeout) {
		execute(task);
	}

	public Future<?> submit(Runnable task) {
		if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
			return ((ExecutorService) this.concurrentExecutor).submit(task);
		} else {
			FutureTask<Object> future = new FutureTask<Object>(task, null);
			doExecute(this.concurrentExecutor, this.taskDecorator, future);
			return future;
		}
	}

	public <T> Future<T> submit(Callable<T> task) {
		if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
			return ((ExecutorService) this.concurrentExecutor).submit(task);
		} else {
			FutureTask<T> future = new FutureTask<T>(task);
			doExecute(this.concurrentExecutor, this.taskDecorator, future);
			return future;
		}
	}

	public ListenableFuture<?> submitListenable(Runnable task) {
		ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
		doExecute(this.concurrentExecutor, this.taskDecorator, future);
		return future;
	}

	public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
		ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
		doExecute(this.concurrentExecutor, this.taskDecorator, future);
		return future;
	}

	/**
	 * Actually execute the given {@code Runnable} (which may be a user-supplied
	 * task or a wrapper around a user-supplied task) with the given executor.
	 * 
	 * @param concurrentExecutor the underlying JDK concurrent executor to delegate
	 *                           to
	 * @param taskDecorator      the specified decorator to be applied, if any
	 * @param runnable           the runnable to execute
	 * @throws RejectedExecutionException if the given runnable cannot be accepted
	 */
	protected void doExecute(Executor concurrentExecutor, TaskDecorator taskDecorator, Runnable runnable)
			throws RejectedExecutionException {
		concurrentExecutor.execute(taskDecorator != null ? taskDecorator.decorate(runnable) : runnable);
	}

}
