package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.kv.TemporaryKeyValueOperationsWrapper;
import io.basc.framework.data.object.TemporaryObjectOperationsWrapper;

public interface TemporaryDataOperationsWrapper<K> extends TemporaryDataOperations<K>, DataOperationsWrapper<K>,
		TemporaryKeyValueOperationsWrapper<K, Object>, TemporaryObjectOperationsWrapper<K> {
	@Override
	TemporaryDataOperations<K> getSourceOperations();

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return DataOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return DataOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		DataOperationsWrapper.super.set(key, value);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryObjectOperationsWrapper.super.setIfAbsent(key, value, exp, expUnit);
	}

	@Override
	default boolean setIfPresent(K key, Object value, long exp, TimeUnit expUnit) {
		return TemporaryObjectOperationsWrapper.super.setIfPresent(key, value, exp, expUnit);
	}

	@Override
	default void set(K key, Object value, long exp, TimeUnit expUnit) {
		TemporaryObjectOperationsWrapper.super.set(key, value, exp, expUnit);
	}

}
