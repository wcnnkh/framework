package io.basc.framework.util.collections;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.ObjectUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 多个键值对
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public interface KeyValues<K, V> extends Elements<KeyValue<K, V>>, Keys<K> {

	@RequiredArgsConstructor
	@Getter
	public static class SimpleKeyValues<K, V, W extends Elements<KeyValue<K, V>>>
			implements KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, W> {
		@NonNull
		private final W source;
	}

	public static interface KeyValuesWrapper<K, V, W extends KeyValues<K, V>>
			extends KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, W> {

		@Override
		default Elements<K> keys() {
			return getSource().keys();
		}
	}

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
