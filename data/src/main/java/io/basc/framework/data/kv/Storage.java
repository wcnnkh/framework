package io.basc.framework.data.kv;

/**
 * 存储
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface Storage<K, V> extends KeyValueOperations<K, V> {

	/**
	 * 可生存时间
	 * 
	 * @param key
	 * @return 毫秒
	 */
	Long ttl(K key);

	/**
	 * @param key
	 * @return
	 */
	boolean touch(K key);

	default V getAndTouch(K key) {
		V value = get(key);
		if (value != null) {
			touch(key);
		}
		return value;
	}
}
