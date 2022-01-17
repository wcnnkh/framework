package io.basc.framework.redis;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface RedisResponse<T> extends Future<T> {
	@Override
	T get() throws RedisSystemException;

	@Override
	T get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException;
}