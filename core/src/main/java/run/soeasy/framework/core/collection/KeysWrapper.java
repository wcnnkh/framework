package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.Wrapper;

public interface KeysWrapper<K, W extends Keys<K>> extends Keys<K>, Wrapper<W> {
	@Override
	default Elements<K> keys() {
		return getSource().keys();
	}

	@Override
	default boolean hasKey(K key) {
		return getSource().hasKey(key);
	}
}