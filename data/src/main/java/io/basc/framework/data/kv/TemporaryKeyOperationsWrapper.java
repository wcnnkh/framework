package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.codec.Encoder;

public interface TemporaryKeyOperationsWrapper<K> extends TemporaryKeyOperations<K>, KeyOperationsWrapper<K> {

	@Override
	TemporaryKeyOperations<K> getSourceOperations();

	@Override
	default boolean expire(K key, long exp, TimeUnit expUnit) {
		Encoder<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().expire(key, exp, expUnit);
		}
		return getSourceOperations().expire(fomatter.encode(key), exp, expUnit);
	}

	@Override
	default boolean touch(K key, long exp, TimeUnit expUnit) {
		Encoder<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().touch(key, exp, expUnit);
		}
		return getSourceOperations().touch(fomatter.encode(key), exp, expUnit);
	}
}
