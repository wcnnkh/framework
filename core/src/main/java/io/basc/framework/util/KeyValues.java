package io.basc.framework.util;

/**
 * 多个键值对
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public interface KeyValues<K, V> extends Listable<KeyValue<K, V>>, Keys<K> {

	@Override
	default Elements<K> fetchKeys() {
		return getElements().map((e) -> e.getKey());
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
