/*
 * Copyright 2002-2017 the original author or authors.
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

package run.soeasy.framework.util.exchange.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.exchange.Listener;
import run.soeasy.framework.util.exchange.Registration;

/**
 * A {@link ListenableFuture} whose value can be set via {@link #set(Object)} or
 * {@link #setException(Throwable)}. It may also get cancelled.
 *
 * <p>
 * Inspired by {@code com.google.common.util.concurrent.SettableFuture}.
 *
 */
public class SettableListenableFuture<T> implements Promise<T> {

	private static class SettableTask<T> extends ListenableFutureTask<T> {

		private volatile Thread completingThread;

		@SuppressWarnings("unchecked")
		public SettableTask() {
			super((Callable<T>) DUMMY_CALLABLE);
		}

		private boolean checkCompletingThread() {
			boolean check = (this.completingThread == Thread.currentThread());
			if (check) {
				this.completingThread = null; // only first match actually
												// counts
			}
			return check;
		}

		@Override
		protected void done() {
			if (!isCancelled()) {
				// Implicitly invoked by set/setException: store current thread
				// for
				// determining whether the given result has actually triggered
				// completion
				// (since FutureTask.set/setException unfortunately don't expose
				// that)
				this.completingThread = Thread.currentThread();
			}
			super.done();
		}

		public boolean setExceptionResult(Throwable exception) {
			setException(exception);
			return checkCompletingThread();
		}

		public boolean setResultValue(T value) {
			set(value);
			return checkCompletingThread();
		}
	}

	private static final Callable<Object> DUMMY_CALLABLE = new Callable<Object>() {
		public Object call() throws Exception {
			throw new IllegalStateException("Should never be called");
		}
	};
	private final SettableTask<T> settableTask = new SettableTask<T>();

	private boolean uncancellable = false;

	public boolean cancel(boolean mayInterruptIfRunning) {
		if (uncancellable) {
			throw new IllegalStateException("uncancellable");
		}

		boolean cancelled = this.settableTask.cancel(mayInterruptIfRunning);
		if (cancelled && mayInterruptIfRunning) {
			interruptTask();
		}
		return cancelled;
	}

	@Override
	public Throwable cause() {
		return settableTask.cause();
	}

	/**
	 * Retrieve the value.
	 * <p>
	 * This method returns the value if it has been set via {@link #set(Object)},
	 * throws an {@link java.util.concurrent.ExecutionException} if an exception has
	 * been set via {@link #setException(Throwable)}, or throws a
	 * {@link java.util.concurrent.CancellationException} if the future has been
	 * cancelled.
	 * 
	 * @return the value associated with this future
	 */
	public T get() throws InterruptedException, ExecutionException {
		return this.settableTask.get();
	}

	/**
	 * Retrieve the value.
	 * <p>
	 * This method returns the value if it has been set via {@link #set(Object)},
	 * throws an {@link java.util.concurrent.ExecutionException} if an exception has
	 * been set via {@link #setException(Throwable)}, or throws a
	 * {@link java.util.concurrent.CancellationException} if the future has been
	 * cancelled.
	 * 
	 * @param timeout the maximum time to wait
	 * @param unit    the unit of the timeout argument
	 * @return the value associated with this future
	 */
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.settableTask.get(timeout, unit);
	}

	@Override
	public T getNow() {
		return settableTask.getNow();
	}

	/**
	 * Subclasses can override this method to implement interruption of the future's
	 * computation. The method is invoked automatically by a successful call to
	 * {@link #cancel(boolean) cancel(true)}.
	 * <p>
	 * The default implementation is empty.
	 */
	protected void interruptTask() {
	}

	@Override
	public boolean isCancellable() {
		return settableTask.isCancellable();
	}

	public boolean isCancelled() {
		return this.settableTask.isCancelled();
	}

	public boolean isDone() {
		return this.settableTask.isDone();
	}

	@Override
	public boolean isSuccess() {
		return settableTask.isSuccess();
	}

	@Override
	public Registration registerListener(Listener<ListenableFuture<? extends T>> listener) {
		return settableTask.registerListener(listener);
	}

	@Override
	public boolean setUncancellable() {
		if (isDone()) {
			return false;
		}
		uncancellable = true;
		return true;
	}

	@Override
	public boolean tryFailure(Throwable cause) {
		Assert.notNull(cause, "Exception must not be null");
		return this.settableTask.setExceptionResult(cause);
	}

	@Override
	public boolean trySuccess(T result) {
		return this.settableTask.setResultValue(result);
	}

}
