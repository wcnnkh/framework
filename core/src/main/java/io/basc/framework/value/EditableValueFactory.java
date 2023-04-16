package io.basc.framework.value;

public interface EditableValueFactory<K> extends DynamicValueFactory<K> {

	void put(K key, Value value);

	boolean putIfAbsent(K key, Value value);

	boolean remove(K key);
}
