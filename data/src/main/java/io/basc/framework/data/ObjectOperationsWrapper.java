package io.basc.framework.data;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;

public interface ObjectOperationsWrapper<K> extends ObjectOperations<K>, KeyOperationsWrapper<K>, ValueOperationsWrapper<K, Object> {

	@Override
	ObjectOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default Codec<K, K> getKeyFomatter() {
		return ValueOperationsWrapper.super.getKeyFomatter();
	}

	@Override
	default void set(K key, Object value) {
		ValueOperationsWrapper.super.set(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> T get(TypeDescriptor type, K key) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Object value = getSourceOperations().get(type, keyFomatter == null ? key : keyFomatter.encode(key));
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return (T) (valueFomatter == null ? value : valueFomatter.decode(value));
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}
}
