package scw.mvc;

import java.util.Enumeration;

public interface AttributeManager {
	Object getAttribute(String name);

	Enumeration<String> getAttributeNames();

	void setAttribute(String name, Object o);

	void removeAttribute(String name);
}
