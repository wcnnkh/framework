package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryValueOperationsWrapper;

public interface TemporaryObjectValueOperationsWrapper<K> extends TemporaryObjectValueOperations<K>,
		ObjectValueOperationsWrapper<K>, TemporaryValueOperationsWrapper<K, Object> {
	@Override
	TemporaryObjectValueOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		return ObjectValueOperationsWrapper.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return ObjectValueOperationsWrapper.super.setIfPresent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		ObjectValueOperationsWrapper.super.set(key, value, valueType);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value) {
		ObjectValueOperationsWrapper.super.set(key, value);
	}

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
}
