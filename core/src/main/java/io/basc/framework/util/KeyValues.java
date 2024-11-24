package io.basc.framework.util;

/**
 * 多个键值对
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public interface KeyValues<K, V> extends Elements<KeyValue<K, V>>, Keys<K> {

	public static <K, V> KeyValues<K, V> of(Elements<KeyValue<K, V>> elements) {
		return new SimpleKeyValues<>(elements);
	}

	@Override
	default Elements<K> keys() {
		return map((e) -> e.getKey());
	}

	/**
	 * 获取值,默认调用{@link #getKeyValues(Object)}
	 * 
	 * @param key
	 * @return
	 */
	default Elements<V> getValues(K key) {
		return getKeyValues(key).map((e) -> e.getValue());
	}

	/**
	 * 获取key对应的键值对
	 * 
	 * @param key
	 * @return
	 */
	default Elements<KeyValue<K, V>> getKeyValues(K key) {
		return filter((keyValue) -> ObjectUtils.equals(key, keyValue.getKey()));
	}
}
