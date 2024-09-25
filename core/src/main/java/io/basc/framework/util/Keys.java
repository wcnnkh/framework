package io.basc.framework.util;

/**
 * 获取所有的key
 * 
 * @author shuchaowen
 *
 * @param <K>
 */
public interface Keys<K> {
	Elements<K> fetchKeys();

	/**
	 * 是否存在此key
	 * 
	 * @param key
	 * @return
	 */
	default boolean hasKey(K key) {
		return fetchKeys().contains(key);
	}
}
