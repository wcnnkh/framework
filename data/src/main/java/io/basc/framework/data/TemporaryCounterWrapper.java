package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;

public interface TemporaryCounterWrapper
		extends CounterWrapper, TemporaryCounter, TemporaryKeyOperationsWrapper<String> {
	@Override
	TemporaryCounter getSourceOperations();

	@Override
	default long decr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().decr(key, delta, initialValue, exp, expUnit);
		}

		return getSourceOperations().decr(formatter.encode(key), delta, initialValue, exp, expUnit);
	}

	@Override
	default long incr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().incr(key, delta, initialValue, exp, expUnit);
		}
		return getSourceOperations().incr(formatter.encode(key), delta, initialValue, exp, expUnit);
	}

	@Override
	default long decr(String key, long delta, long initialValue) {
		return TemporaryCounter.super.decr(key, delta, initialValue);
	}

	@Override
	default long incr(String key, long delta, long initialValue) {
		return TemporaryCounter.super.incr(key, delta, initialValue);
	}
}
