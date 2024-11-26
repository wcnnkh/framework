package io.basc.framework.data.kv;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.codec.Encoder;

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
