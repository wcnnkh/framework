package scw.core.attribute;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleAttributeManager implements AttributeManager {
	private Map<String, Object> attributeMap;

	public Object getAttribute(String name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	protected Map<String, Object> createAttributeMap() {
		return new LinkedHashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return (Enumeration<String>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
	}

	public void setAttribute(String name, Object o) {
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
