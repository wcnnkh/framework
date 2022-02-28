package io.basc.framework.data;

import io.basc.framework.codec.Codec;
import io.basc.framework.lang.Nullable;

public interface KeyOperationsWrapper<K> extends KeyOperations<K> {

	KeyOperations<K> getSourceOperations();

	@Nullable
	default Codec<K, K> getKeyFomatter() {
		return null;
	}

	@Override
	default boolean delete(K key) {
		Codec<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().delete(key);
		}

		return getSourceOperations().delete(fomatter.encode(key));
	}

	@Override
	default boolean exists(K key) {
		Codec<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().exists(key);
		}

		return getSourceOperations().exists(fomatter.encode(key));
	}
}
