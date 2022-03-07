package io.basc.framework.data.object;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.KeyOperationsWrapper;

public interface ObjectOperationsWrapper<K>
		extends ObjectOperations<K>, KeyOperationsWrapper<K>, ObjectValueOperationsWrapper<K> {

	@Override
	ObjectOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default Codec<K, K> getKeyFomatter() {
		return null;
	}

	@Override
	default Codec<Object, Object> getValueFomatter() {
		return null;
	}

	@Override
	default void set(K key, Object value) {
		ObjectValueOperationsWrapper.super.set(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> T get(TypeDescriptor type, K key) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Object value = getSourceOperations().get(type, keyFomatter == null ? key : keyFomatter.encode(key));
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return (T) (valueFomatter == null ? value : valueFomatter.decode(value));
	}
}
