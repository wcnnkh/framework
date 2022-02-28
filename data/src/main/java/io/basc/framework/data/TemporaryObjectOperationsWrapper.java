package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;

public interface TemporaryObjectOperationsWrapper<K> extends ObjectOperationsWrapper<K>, TemporaryObjectOperations<K>,
		TemporaryKeyOperationsWrapper<K>, TemporaryValueOperationsWrapper<K, Object> {
	@Override
	TemporaryObjectOperations<K> getSourceOperations();

	@Override
	default void set(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		// TODO Auto-generated method stub
		return ObjectOperationsWrapper.super.setIfAbsent(key, value, valueType);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		// TODO Auto-generated method stub
		return TemporaryObjectOperations.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return ObjectOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		return ObjectOperationsWrapper.super.setIfPresent(key, value, valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryObjectOperations.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return ObjectOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value) {
		ObjectOperationsWrapper.super.set(key, value);
	}

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		ObjectOperationsWrapper.super.set(key, value, valueType);
	}
}
