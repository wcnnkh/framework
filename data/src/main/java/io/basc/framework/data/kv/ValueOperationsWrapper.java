package io.basc.framework.data.kv;

import io.basc.framework.codec.Codec;
import io.basc.framework.lang.Nullable;

public interface ValueOperationsWrapper<K, V> extends ValueOperations<K, V> {
	ValueOperations<K, V> getSourceOperations();

	@Nullable
	default Codec<K, K> getKeyFomatter() {
		return null;
	}

	@Nullable
	default Codec<V, V> getValueFomatter() {
		return null;
	}

	@Override
	default void set(K key, V value) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfAbsent(K key, V value) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}

	@Override
	default boolean setIfPresent(K key, V value) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<V, V> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value));
	}
}
