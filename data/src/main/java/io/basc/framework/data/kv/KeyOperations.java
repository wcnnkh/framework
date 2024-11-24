package io.basc.framework.data.kv;

import java.util.Collection;

import io.basc.framework.util.collect.CollectionUtils;

public interface KeyOperations<K> {
	boolean exists(K key);

	boolean delete(K key);

	default void delete(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return;
		}

		for (K key : keys) {
			delete(key);
		}
	}
}
