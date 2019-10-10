package scw.core.attribute;

import java.util.Enumeration;

public interface AttributesReadOnly<T> {
	T getAttribute(String name);

	Enumeration<String> getAttributeNames();
}
