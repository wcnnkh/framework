package io.basc.framework.util.attribute;

import java.util.Enumeration;

public interface Attributes<K, V> {
	V getAttribute(K name);
	
	Enumeration<K> getAttributeNames();
}
