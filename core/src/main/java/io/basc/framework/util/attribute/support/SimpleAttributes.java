package io.basc.framework.util.attribute.support;

import io.basc.framework.core.utils.ObjectUtils;
import io.basc.framework.util.attribute.Attributes;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleAttributes<K, V> implements Attributes<K, V> {
	private Map<K, V> attributeMap;

	public SimpleAttributes() {
	}

	public SimpleAttributes(Map<K, V> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public SimpleAttributes(SimpleAttributes<K, V> attributes) {
		this.attributeMap = attributes.attributeMap;
	}

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

	@Override
	public String toString() {
		return attributeMap.toString();
	}

	@Override
	public int hashCode() {
		return attributeMap == null ? 0 : attributeMap.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof SimpleAttributes) {
			return ObjectUtils.nullSafeEquals(attributeMap, ((SimpleAttributes) obj).attributeMap);
		}

		return false;
	}
}
