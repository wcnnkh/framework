package io.basc.framework.util.config;

import java.util.NoSuchElementException;

import io.basc.framework.util.KeyValues;
import io.basc.framework.util.NoUniqueElementException;

public interface ListablePropertyFactory<K, V> extends PropertyFactory<K, V>, KeyValues<K, V> {
	@Override
	default V getProperty(K key) throws NoSuchElementException, NoUniqueElementException {
		return getElements(key).getUnique().getValue();
	}

	@Override
	default boolean isEmpty() {
		return KeyValues.super.isEmpty();
	}

	@Override
	default long size() {
		return fetchKeys().distinct().count();
	}
}
