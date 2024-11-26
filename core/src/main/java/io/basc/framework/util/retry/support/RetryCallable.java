package io.basc.framework.util.retry.support;

import java.util.concurrent.Callable;

import io.basc.framework.util.retry.RetryCallback;
import io.basc.framework.util.retry.RetryOperations;

public class RetryCallable<V> implements Callable<V> {
	private final RetryCallback<V, ? extends Exception> retryCallback;
	private final RetryOperations retryOperations;

	public RetryCallable(RetryOperations retryOperations, RetryCallback<V, ? extends Exception> retryCallback) {
		this.retryOperations = retryOperations;
		this.retryCallback = retryCallback;
	}

	public RetryCallable(RetryOperations retryOperations, Callable<V> callable) {
		this(retryOperations, new DefaultRetryCallback<V>(callable));
	}

	public RetryCallable(RetryOperations retryOperations, Runnable runnable, V result) {
		this(retryOperations, new DefaultRetryCallback<V>(runnable, result));
	}

	public V call() throws Exception {
		return retryOperations.execute(retryCallback);
	}

}
