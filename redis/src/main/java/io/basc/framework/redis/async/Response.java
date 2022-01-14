package io.basc.framework.redis.async;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Response<T> extends Future<T> {
	@Override
	T get();

	@Override
	T get(long timeout, TimeUnit unit) throws TimeoutException;
}