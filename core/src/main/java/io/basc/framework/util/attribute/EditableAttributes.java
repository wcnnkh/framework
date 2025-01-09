package io.basc.framework.util.attribute;

import io.basc.framework.util.collection.Elements;

public interface EditableAttributes<K, V> extends Attributes<K, V> {
	void setAttribute(K name, V value);

	void removeAttribute(K name);

	default void setAttributes(Attributes<K, ? extends V> attributes) {
		Elements<? extends K> keys = attributes.getAttributeNames();
		keys.forEach((key) -> setAttribute(key, attributes.getAttribute(key)));
	}
}
