package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

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
	 * 获取生存时间单位，默认为毫秒
	 * 
	 * @return
	 */
	default TimeUnit getSurvivalTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	/**
	 * 获取剩余生存时间
	 * 
	 * @param key
	 * @return
	 */
	Long getRemainingSurvivalTime(K key);

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
