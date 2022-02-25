package io.basc.framework.data.storage;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyOperationsWrapper;
import io.basc.framework.data.kv.TemporaryValueOperationsWrapper;

public interface TemporaryStorageWrapper<K> extends StorageWrapper<K>, TemporaryStorage<K>,
		TemporaryKeyOperationsWrapper<K>, TemporaryValueOperationsWrapper<K, Object> {
	@Override
	TemporaryStorage<K> getSourceOperations();

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
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryValueOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryValueOperationsWrapper.super.set(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return StorageWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return StorageWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		StorageWrapper.super.set(key, value);
	}
}
