package io.basc.framework.redis.convert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSystemException;
import io.basc.framework.util.function.Processor;

public class ConvertibleRedisResponse<SV, V> implements RedisResponse<V> {
	private final RedisResponse<SV> source;
	private final Processor<? super SV, ? extends V, ? extends RedisSystemException> converter;

	public ConvertibleRedisResponse(RedisResponse<SV> source,
			Processor<? super SV, ? extends V, ? extends RedisSystemException> converter) {
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
		return converter.process(value);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws TimeoutException, RedisSystemException {
		SV value = source.get(timeout, unit);
		return converter.process(value);
	}
}
