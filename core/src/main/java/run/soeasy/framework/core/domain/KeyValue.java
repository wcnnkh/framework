package run.soeasy.framework.core.domain;

public interface KeyValue<K, V> {

	K getKey();

	V getValue();

	default KeyValue<V, K> reversed() {
		return new ReversedKeyValue<>(this);
	}

	public static <K, V> KeyValue<K, V> of(K key, V value) {
		return new CustomizeKeyValue<>(key, value);
	}
}
