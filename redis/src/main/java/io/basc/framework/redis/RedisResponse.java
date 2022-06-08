package io.basc.framework.redis;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.redis.convert.ConvertibleRedisResponse;
import io.basc.framework.util.stream.Processor;

public interface RedisResponse<T> extends Future<T> {
	@Override
	T get() throws RedisSystemException;

	@Override
	T get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException;

	default <R> RedisResponse<R> map(Processor<T, R, ? extends RedisSystemException> converter) {
		return new ConvertibleRedisResponse<>(this, converter);
	}
}