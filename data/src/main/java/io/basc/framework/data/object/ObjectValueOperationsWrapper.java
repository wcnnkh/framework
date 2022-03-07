package io.basc.framework.data.object;

import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.ValueOperationsWrapper;

public interface ObjectValueOperationsWrapper<K> extends ObjectValueOperations<K>, ValueOperationsWrapper<K, Object> {

	@Override
	ObjectValueOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		ValueOperationsWrapper.super.set(key, value);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}
}
