package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.domain.KeyValue;

@FunctionalInterface
public interface Dictionary<K, V, E extends KeyValue<K, V>> extends KeyValues<K, V>, Listable<E> {
	/**
	 * 化为Array结构
	 * 
	 * @param uniqueness 是否有唯一性
	 * @return
	 */
	default Dictionary<K, V, E> asArray(boolean uniqueness) {
		return new ArrayDictionary<>(this, uniqueness);
	}

	default boolean isMap() {
		return false;
	}

	/**
	 * 化为Map结构
	 * 
	 * @param uniqueness 是否有唯一性
	 * @return
	 */
	default Dictionary<K, V, E> asMap(boolean uniqueness) {
		return new MapDictionary<>(this, true, uniqueness);
	}

	default E getElement(int index) {
		return getElements().get(index);
	}

	@Override
	default Elements<V> getValues(K key) {
		return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
	}

	default boolean isArray() {
		return false;
	}

	@Override
	default Elements<K> keys() {
		return getElements().map((e) -> e.getKey());
	}

	default int size() {
		return Math.toIntExact(getElements().count());
	}
}
