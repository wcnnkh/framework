package scw.data.utils;

public interface Map<K, V> {
	int size();

	boolean isEmpty();

	V get(K key);

	boolean remove(K key);

	boolean containsKey(K key);

	void put(K key, V value);

	boolean putIfAbsent(K key, V value);

	void putAll(java.util.Map<? extends K, ? extends V> m);

	java.util.Map<K, V> asMap();

	V getAndRemove(K key);

	V getAndPut(K key, V value);

	V getAndPutIfAbsent(K key, V value);
}
