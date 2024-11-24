package io.basc.framework.util;

public interface KeyValuesWrapper<K, V, W extends KeyValues<K, V>>
		extends KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, W> {

	@Override
	default Elements<K> keys() {
		return getSource().keys();
	}
}
