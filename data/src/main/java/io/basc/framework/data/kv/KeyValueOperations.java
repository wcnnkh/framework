package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.util.CollectionUtils;

public interface KeyValueOperations<K, V> extends KeyOperations<K> {
	V get(K key);

	default Map<K, V> get(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Map<K, V> map = new LinkedHashMap<K, V>(keys.size());
		for (K key : keys) {
			V value = get(key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

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