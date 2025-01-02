package io.basc.framework.redis;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.redis.convert.ConvertibleRedisResponse;
import io.basc.framework.util.Function;

public interface RedisResponse<T> extends Future<T> {
	@Override
	T get() throws RedisSystemException;

	@Override
	T get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException;

	default <R> RedisResponse<R> map(Function<? super T, ? extends R, ? extends RedisSystemException> converter) {
		return new ConvertibleRedisResponse<>(this, converter);
	}
}