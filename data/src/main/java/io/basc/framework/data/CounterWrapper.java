package io.basc.framework.data;

import io.basc.framework.codec.Codec;

public interface CounterWrapper extends Counter, KeyOperationsWrapper<String> {
	@Override
	Counter getSourceOperations();

	default Codec<String, String> getKeyCodec() {
		return null;
	}

	@Override
	default long decr(String key, long delta) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().decr(key, delta);
		}
		return getSourceOperations().decr(formatter.encode(key), delta);
	}

	@Override
	default long decr(String key, long delta, long initialValue) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().decr(key, delta, initialValue);
		}
		return getSourceOperations().decr(formatter.encode(key), delta, initialValue);
	}

	@Override
	default long incr(String key, long delta) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().incr(key, delta);
		}
		return getSourceOperations().incr(formatter.encode(key), delta);
	}

	@Override
	default long incr(String key, long delta, long initialValue) {
		Codec<String, String> formatter = getKeyCodec();
		if (formatter == null) {
			return getSourceOperations().incr(key, delta, initialValue);
		}
		return getSourceOperations().incr(formatter.encode(key), delta, initialValue);
	}
}
