package io.basc.framework.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.lang.NestedExceptionUtils;

public class DefaultRedisResponse<T> extends FutureTask<T> implements RedisResponse<T> {

	public DefaultRedisResponse(Callable<T> callable) {
		super(callable);
	}

	public DefaultRedisResponse(Runnable runnable, T result) {
		super(runnable, result);
	}

	@Override
	public T get() throws RedisSystemException {
		try {
			return super.get();
		} catch (InterruptedException e) {
			throw new IllegalStateException("Should never get here", e);
		} catch (ExecutionException e) {
			throw new RedisSystemException(NestedExceptionUtils.getMostSpecificCause(e));
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException {
		try {
			return super.get(timeout, unit);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Should never get here", e);
		} catch (ExecutionException e) {
			throw new RedisSystemException(NestedExceptionUtils.getMostSpecificCause(e));
		}
	}
}
