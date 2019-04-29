package scw.data.utils.map;

public interface Map<K, V> {
	
	V get(K key);

	/**
	 * 如果key不存在则返回false
	 * @param key
	 * @return
	 */
	boolean remove(K key);

	boolean containsKey(Object key);

	void put(K key, V value);
	
	/**
	 * 不存在就添加
	 * @param key
	 * @param value
	 * @return 是否添加成功
	 */
	boolean putIfAbsent(K key, V value);
}
