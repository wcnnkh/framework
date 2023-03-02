package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface Storage<K, V> extends KeyValueOperations<K, V> {

	default TimeUnit getSurvivalTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	Long getRemainingSurvivalTime(K key);

	boolean touch(K key);

	default V getAndTouch(K key) {
		V value = get(key);
		if (value != null) {
			touch(key);
		}
		return value;
	}
}
