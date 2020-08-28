package scw.util.attribute;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleAttributes<K, V> implements Attributes<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<K, V> attributeMap;

	public V getAttribute(K name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	protected Map<K, V> createAttributeMap() {
		return new LinkedHashMap<K, V>(4);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<K> getAttributeNames() {
		return (Enumeration<K>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
	}

	public void setAttribute(K name, V o) {
		if (attributeMap == null) {
			attributeMap = createAttributeMap();
		}

		attributeMap.put(name, o);
	}

	public void removeAttribute(K name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

}
