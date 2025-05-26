package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.domain.KeyValue;

@FunctionalInterface
public interface Dictionary<K, V, E extends KeyValue<K, V>> extends KeyValues<K, V>, Listable<E> {
	default Dictionary<K, V, E> asArray() {
		return this;
	}

	default Dictionary<K, V, E> asMap() {
		return new MapDictionary<>(this);
	}

	default E getElement(int index) {
		return getElements().get(index);
	}

	@Override
	default Elements<V> getValues(K key) {
		return getElements().filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
	}

	default boolean isArray() {
		return true;
	}

	default boolean isMap() {
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
