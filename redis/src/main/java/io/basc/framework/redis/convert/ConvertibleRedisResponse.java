package io.basc.framework.redis.convert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.convert.Converter;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSystemException;

public class ConvertibleRedisResponse<SV, V> implements RedisResponse<V> {
	private final RedisResponse<SV> source;
	private final Converter<SV, V> converter;

	public ConvertibleRedisResponse(RedisResponse<SV> source, Converter<SV, V> converter) {
		this.source = source;
		this.converter = converter;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return source.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return source.isCancelled();
	}

	@Override
	public boolean isDone() {
		return source.isDone();
	}

	@Override
	public V get() throws RedisSystemException {
		SV value = source.get();
		return converter.convert(value);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException {
		SV value = source.get(timeout, unit);
		return converter.convert(value);
	}
}
