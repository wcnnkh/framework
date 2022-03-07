package io.basc.framework.data.kv;

import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.Nullable;

public interface KeyOperationsWrapper<K> extends KeyOperations<K> {

	KeyOperations<K> getSourceOperations();

	@Nullable
	default Encoder<K, K> getKeyFomatter() {
		return null;
	}

	@Override
	default boolean delete(K key) {
		Encoder<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().delete(key);
		}

		return getSourceOperations().delete(fomatter.encode(key));
	}

	@Override
	default boolean exists(K key) {
		Encoder<K, K> fomatter = getKeyFomatter();
		if (fomatter == null) {
			return getSourceOperations().exists(key);
		}

		return getSourceOperations().exists(fomatter.encode(key));
	}
}
