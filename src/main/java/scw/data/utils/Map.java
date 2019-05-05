package scw.data.utils;

public interface Map<K, V> {
	int size();

	boolean isEmpty();

	V get(K key);

	V remove(K key);

	boolean containsKey(K key);

	V put(K key, V value);

	/**
	 * 不存在就添加
	 * 
	 * @param key
	 * @param value
	 * @return 如果已经存在旧的值就返回旧值
	 */
	V putIfAbsent(K key, V value);

	void putAll(java.util.Map<? extends K, ? extends V> m);

	java.util.Map<K, V> asMap();
}
