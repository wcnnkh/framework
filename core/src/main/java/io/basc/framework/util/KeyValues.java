package io.basc.framework.util;

/**
 * 一对多
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public interface KeyValues<K, V> extends Listable<KeyValue<K, V>> {

	/**
	 * 所有的key
	 * 
	 * @return
	 */
	default Elements<K> keys() {
		return getElements().map((e) -> e.getKey());
	}

	/**
	 * 是否存在此key
	 * 
	 * @param key
	 * @return
	 */
	default boolean hasKey(K key) {
		return !getElements(key).isEmpty();
	}

	/**
	 * 获取key对应的键值对
	 * 
	 * @param key
	 * @return
	 */
	default Elements<KeyValue<K, V>> getElements(K key) {
		return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey()));
	}
}
