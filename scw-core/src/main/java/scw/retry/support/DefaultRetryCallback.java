package scw.retry.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import scw.retry.RetryCallback;
import scw.retry.RetryContext;

public class DefaultRetryCallback<T> implements RetryCallback<T, Exception>{
	private final Callable<T> callable;
	
	public DefaultRetryCallback(Callable<T> callable){
		this.callable = callable;
	}
	
	public DefaultRetryCallback(Runnable runnable, T result){
		this.callable = Executors.callable(runnable, result);
	}
	
	public T doWithRetry(RetryContext context) throws Exception {
		return callable.call();
	}

}
