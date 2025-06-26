package run.soeasy.framework.core.domain;

import java.util.Map.Entry;

import lombok.NonNull;

public interface KeyValue<K, V> {

	K getKey();

	V getValue();

	default KeyValue<V, K> reversed() {
		return new ReversedKeyValue<>(this);
	}

	public static <K, V> KeyValue<K, V> of(K key, V value) {
		return new CustomizeKeyValue<>(key, value);
	}

	public static <K, V> KeyValue<K, V> wrap(@NonNull Entry<K, V> entry) {
		return (EntryWrapper<K, V, Entry<K, V>>) () -> entry;
	}
}
