package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.KeyValue;

public interface DictionaryWrapper<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
		extends Dictionary<K, V, E>, KeyValuesWrapper<K, V, W>, ListableWrapper<E, W> {
	@Override
	default Elements<K> keys() {
		return getSource().keys();
	}

	@Override
	default Elements<V> getValues(K key) {
		return getSource().getValues(key);
	}

	@Override
	default E getElement(int index) {
		return getSource().getElement(index);
	}

	@Override
	default boolean isMap() {
		return getSource().isMap();
	}

	@Override
	default boolean isArray() {
		return getSource().isArray();
	}

	@Override
	default Dictionary<K, V, E> asMap(boolean uniqueness) {
		return getSource().asMap(uniqueness);
	}

	@Override
	default Dictionary<K, V, E> asArray(boolean uniqueness) {
		return getSource().asArray(uniqueness);
	}

	@Override
	default int size() {
		return getSource().size();
	}
}