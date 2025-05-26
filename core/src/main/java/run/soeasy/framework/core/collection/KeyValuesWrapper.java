package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.collection.Keys.KeysWrapper;

public interface KeyValuesWrapper<K, V, W extends KeyValues<K, V>> extends KeyValues<K, V>, KeysWrapper<K, W> {
	@Override
	default Elements<V> getValues(K key) {
		return getSource().getValues(key);
	}
}