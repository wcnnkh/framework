package io.basc.framework.data.kv;

import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.Nullable;

public interface ValueOperationsWrapper<K, V> extends ValueOperations<K, V> {
	ValueOperations<K, V> getSourceOperations();

	@Nullable
	default Encoder<K, K> getKeyFomatter() {
		return null;
	}

	@Nullable
	default Encoder<V, V> getValueFomatter() {
		return null;
	}

	@Override
	default void set(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfAbsent(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfPresent(K key, V value) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}
}
