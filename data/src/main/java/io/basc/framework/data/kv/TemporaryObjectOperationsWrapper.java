package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.core.convert.TypeDescriptor;

public interface TemporaryObjectOperationsWrapper<K> extends TemporaryObjectOperations<K>, ObjectOperationsWrapper<K>,
		TemporaryKeyValueOperationsWrapper<K, Object> {
	@Override
	TemporaryObjectOperations<K> getSourceOperations();

	@Override
	default void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> T getAndTouch(TypeDescriptor type, K key, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		T value = getSourceOperations().getAndTouch(type, keyFomatter == null ? key : keyFomatter.encode(key), exp,
				expUnit);
		return valueFomatter == null ? value : (T) valueFomatter.decode(value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryKeyValueOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryKeyValueOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryKeyValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}
}
