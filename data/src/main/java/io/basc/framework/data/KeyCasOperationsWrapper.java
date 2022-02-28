package io.basc.framework.data;

import io.basc.framework.codec.Codec;

public interface KeyCasOperationsWrapper<K> extends KeyCasOperations<K>, KeyOperationsWrapper<K> {

	KeyCasOperations<K> getSourceOperations();

	@Override
	default boolean delete(K key, long cas) {
		Codec<K, K> fomatter = getKeyFomatter();
		return getSourceOperations().delete(fomatter == null ? key : fomatter.encode(key), cas);
	}
}
