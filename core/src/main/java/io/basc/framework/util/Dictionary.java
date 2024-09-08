package io.basc.framework.util;

public interface Dictionary<K, V> extends Document<KeyValue<K, V>> {

	default Elements<KeyValue<K, V>> getElements(K key) {
		return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey()));
	}
}
