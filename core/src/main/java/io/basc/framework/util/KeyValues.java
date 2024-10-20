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
		return new KeyValuesWrapper<K, V>() {
			@Override
			public Elements<KeyValue<K, V>> getSource() {
				return elements;
			}
		};
	}

	@Override
	default Elements<K> keys() {
		return map((e) -> e.getKey());
	}

	/**
	 * 获取key对应的键值对
	 * 
	 * @param keys
	 * @return
	 */
	default KeyValues<K, V> gets(Iterable<? extends K> keys) {
		Elements<KeyValue<K, V>> elements = filter((keyValue) -> {
			for (K key : keys) {
				if (ObjectUtils.equals(key, keyValue.getKey())) {
					return true;
				}
			}
			return false;
		});

		return KeyValues.of(elements);
	}
}
