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

package scw.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import scw.lang.RequiredJavaVersion;

/**
 * Adapts a {@link CompletableFuture} or {@link CompletionStage} into a Spring
 * {@link ListenableFuture}.
 *
 */

@RequiredJavaVersion(8)
public class CompletableToListenableFutureAdapter<T> implements ListenableFuture<T> {

	private final CompletableFuture<T> completableFuture;

	private final ListenableFutureCallbackRegistry<T> callbacks = new ListenableFutureCallbackRegistry<T>();

	/**
	 * Create a new adapter for the given {@link CompletionStage}.
	 */
	public CompletableToListenableFutureAdapter(CompletionStage<T> completionStage) {
		this(completionStage.toCompletableFuture());
	}

	/**
	 * Create a new adapter for the given {@link CompletableFuture}.
	 */
	public CompletableToListenableFutureAdapter(CompletableFuture<T> completableFuture) {
		this.completableFuture = completableFuture;
		this.completableFuture.handle(new BiFunction<T, Throwable, Object>() {
			public Object apply(T result, Throwable ex) {
				if (ex != null) {
					callbacks.failure(ex);
				} else {
					callbacks.success(result);
				}
				return null;
			}
		});
	}

	public void addCallback(ListenableFutureCallback<? super T> callback) {
		this.callbacks.addCallback(callback);
	}

	public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
		if(successCallback != null){
			this.callbacks.addSuccessCallback(successCallback);
		}
		
		if(failureCallback != null){
			this.callbacks.addFailureCallback(failureCallback);
		}
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.completableFuture.cancel(mayInterruptIfRunning);
	}

	public boolean isCancelled() {
		return this.completableFuture.isCancelled();
	}

	public boolean isDone() {
		return this.completableFuture.isDone();
	}

	public T get() throws InterruptedException, ExecutionException {
		return this.completableFuture.get();
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.completableFuture.get(timeout, unit);
	}

}
