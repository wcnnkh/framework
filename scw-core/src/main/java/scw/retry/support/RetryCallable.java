package scw.retry.support;

import java.util.concurrent.Callable;

import scw.retry.RetryCallback;
import scw.retry.RetryOperations;

public class RetryCallable<V> implements Callable<V> {
	private final RetryCallback<V, ? extends Exception> retryCallback;
	private final RetryOperations retryOperations;

	public RetryCallable(RetryOperations retryOperations,
			RetryCallback<V, ? extends Exception> retryCallback) {
		this.retryOperations = retryOperations;
		this.retryCallback = retryCallback;
	}
	
	public RetryCallable(RetryOperations retryOperations, Callable<V> callable) {
		this(retryOperations, new DefaultRetryCallback<V>(callable));
	}
	
	public RetryCallable(RetryOperations retryOperations, Runnable runnable, V result){
		this(retryOperations, new DefaultRetryCallback<V>(runnable, result));
	}

	public V call() throws Exception {
		return retryOperations.execute(retryCallback);
	}

}
