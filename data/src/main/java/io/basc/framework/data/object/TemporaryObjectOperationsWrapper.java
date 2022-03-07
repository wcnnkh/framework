package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyOperationsWrapper;

public interface TemporaryObjectOperationsWrapper<K> extends ObjectOperationsWrapper<K>, TemporaryObjectOperations<K>,
		TemporaryKeyOperationsWrapper<K>, TemporaryObjectValueOperationsWrapper<K> {
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

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryObjectValueOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryObjectValueOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryObjectValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value) {
		ObjectOperationsWrapper.super.set(key, value);
	}
}
