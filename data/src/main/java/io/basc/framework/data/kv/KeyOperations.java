package io.basc.framework.data.kv;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;

public interface KeyOperations<K> {

	public static interface KeyOperationsWrapper<K, W extends KeyOperations<K>> extends KeyOperations<K>, Wrapper<W> {

		@Override
		default boolean delete(K key) {
			return getSource().delete(key);
		}

		@Override
		default boolean exists(K key) {
			return getSource().exists(key);
		}

		@Override
		default void delete(Elements<K> keys) {
			getSource().delete(keys);
		}
	}
	
	boolean exists(K key);

	boolean delete(K key);

	default void delete(Elements<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return;
		}

		for (K key : keys) {
			delete(key);
		}
	}
}
