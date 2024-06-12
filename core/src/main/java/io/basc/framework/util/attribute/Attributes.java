package io.basc.framework.util.attribute;

import io.basc.framework.util.element.Elements;

public interface Attributes<K, V> {
	V getAttribute(K name);

	Elements<K> getAttributeNames();
}
