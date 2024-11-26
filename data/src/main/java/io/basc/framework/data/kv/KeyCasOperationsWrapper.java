package io.basc.framework.data.kv;

import io.basc.framework.util.codec.Encoder;

public interface KeyCasOperationsWrapper<K> extends KeyCasOperations<K>, KeyOperationsWrapper<K> {

	KeyCasOperations<K> getSourceOperations();

	@Override
	default boolean delete(K key, long cas) {
		Encoder<K, K> fomatter = getKeyFomatter();
		return getSourceOperations().delete(fomatter == null ? key : fomatter.encode(key), cas);
	}
}
