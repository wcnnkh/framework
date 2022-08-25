package io.basc.framework.util.attribute;

import java.util.Enumeration;

public interface EditableAttributes<K, V> extends Attributes<K, V> {
	void setAttribute(K name, V value);

	void removeAttribute(K name);

	default void setAttributes(Attributes<K, ? extends V> attributes) {
		Enumeration<? extends K> keys = attributes.getAttributeNames();
		while (keys.hasMoreElements()) {
			K key = keys.nextElement();
			setAttribute(key, attributes.getAttribute(key));
		}
	}
}
