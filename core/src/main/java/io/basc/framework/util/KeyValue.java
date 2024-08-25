package io.basc.framework.util;

public interface KeyValue<K, V> {
	K getKey();

	V getValue();

	public static <K, V> KeyValue<K, V> of(K key, V value) {
		return new StandardKeyValue<>(key, value);
	}
}
