/*
 * Copyright 2002-2013 the original author or authors.
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

package io.basc.framework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 * Extended interface for asynchronous {@link TaskExecutor} implementations,
 * offering an overloaded {@link #execute(Runnable, long)} variant with a start
 * timeout parameter as well support for {@link java.util.concurrent.Callable}.
 *
 * <p>
 * Note: The {@link java.util.concurrent.Executors} class includes a set of
 * methods that can convert some other common closure-like objects, for example,
 * {@link java.security.PrivilegedAction} to {@link Callable} before executing
 * them.
 *
 * <p>
 * Implementing this interface also indicates that the
 * {@link #execute(Runnable)} method will not execute its Runnable in the
 * caller's thread but rather asynchronously in some other thread.
 *
 * @see SimpleAsyncExecutor
 * @see java.util.concurrent.Callable
 * @see java.util.concurrent.Executors
 */
public interface AsyncExecutor extends Executor {
	
	@Override
	default void execute(Runnable command) throws RejectedExecutionException {
		submit(command);
	}

	/**
	 * Submit a Runnable task for execution, receiving a Future representing that
	 * task. The Future will return a {@code null} result upon completion.
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @return a Future representing pending completion of the task
	 */
	default Future<?> submit(Runnable task) throws RejectedExecutionException {
		return submit(Executors.callable(task, null));
	}

	/**
	 * Submit a Callable task for execution, receiving a Future representing that
	 * task. The Future will return the Callable's result upon completion.
	 * 
	 * @param task the {@code Callable} to execute (never {@code null})
	 * @return a Future representing pending completion of the task
	 */
	<V> Future<V> submit(Callable<V> task) throws RejectedExecutionException;

	/**
	 * Submit a {@code Runnable} task for execution, receiving a
	 * {@code ListenableFuture} representing that task. The Future will return a
	 * {@code null} result upon completion.
	 * 
	 * @param task the {@code Runnable} to execute (never {@code null})
	 * @return a {@code ListenableFuture} representing pending completion of the
	 *         task
	 */
	default ListenableFuture<?> submitListenable(Runnable task) throws RejectedExecutionException {
		ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
		submit(future);
		return future;
	}

	/**
	 * Submit a {@code Callable} task for execution, receiving a
	 * {@code ListenableFuture} representing that task. The Future will return the
	 * Callable's result upon completion.
	 * 
	 * @param task the {@code Callable} to execute (never {@code null})
	 * @return a {@code ListenableFuture} representing pending completion of the
	 *         task
	 */
	default <V> ListenableFuture<V> submitListenable(Callable<V> task) throws RejectedExecutionException {
		ListenableFutureTask<V> future = new ListenableFutureTask<V>(task);
		submit(future);
		return future;
	}
}
