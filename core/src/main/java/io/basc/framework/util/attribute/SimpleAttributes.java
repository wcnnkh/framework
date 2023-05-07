package io.basc.framework.util.attribute;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class SimpleAttributes<K, V> implements EditableAttributes<K, V> {
	private Map<K, V> attributeMap;

	public SimpleAttributes() {
	}

	public SimpleAttributes(Map<K, V> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public V getAttribute(K name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration<K> getAttributeNames() {
		return (Enumeration<K>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
	}

	public void setAttribute(K name, V o) {
		if (attributeMap == null) {
			attributeMap = new LinkedHashMap<>(8);
		}

		attributeMap.put(name, o);
	}

	public void removeAttribute(K name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

	public void clear() {
		if (attributeMap != null) {
			attributeMap.clear();
		}
	}
}
