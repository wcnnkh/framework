package io.basc.framework.data.kv;

public interface ValueOperations<K, V> {

	/**
	 * 如果不存在就设置
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean setIfAbsent(K key, V value);

	/**
	 * 如果存在就设置
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean setIfPresent(K key, V value);

	/**
	 * 设置
	 * 
	 * @param key
	 * @param value
	 */
	void set(K key, V value);
}
