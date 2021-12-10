package io.basc.framework.retry.support;

import io.basc.framework.retry.RetryCallback;
import io.basc.framework.retry.RetryContext;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DefaultRetryCallback<T> implements RetryCallback<T, Exception> {
	private final Callable<T> callable;

	public DefaultRetryCallback(Callable<T> callable) {
		this.callable = callable;
	}

	public DefaultRetryCallback(Runnable runnable, T result) {
		this.callable = Executors.callable(runnable, result);
	}

	public T doWithRetry(RetryContext context) throws Exception {
		return callable.call();
	}

}
