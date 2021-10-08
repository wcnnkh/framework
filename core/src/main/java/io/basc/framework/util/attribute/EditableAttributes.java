package io.basc.framework.util.attribute;

public interface EditableAttributes<K, V> extends Attributes<K, V> {
	void setAttribute(K name, V value);

	void removeAttribute(K name);
}
