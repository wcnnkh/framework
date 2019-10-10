package scw.core.attribute;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleAttributes<T> implements Attributes<T> {
	private Map<String, T> attributeMap;

	public T getAttribute(String name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	protected Map<String, T> createAttributeMap() {
		return new LinkedHashMap<String, T>();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return (Enumeration<String>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
	}

	public void setAttribute(String name, T o) {
		if (attributeMap == null) {
			attributeMap = createAttributeMap();
		}

		attributeMap.put(name, o);
	}

	public void removeAttribute(String name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

}
