package scw.util.attribute;

import java.util.Enumeration;

public interface AttributesReadOnly<K, V> {
	V getAttribute(K name);

	Enumeration<K> getAttributeNames();
}
