package io.basc.framework.util.retry.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import io.basc.framework.util.retry.RetryCallback;
import io.basc.framework.util.retry.RetryContext;

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
