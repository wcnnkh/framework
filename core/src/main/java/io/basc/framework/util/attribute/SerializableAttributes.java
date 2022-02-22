package io.basc.framework.util.attribute;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

public class SerializableAttributes<K, V> implements EditableAttributes<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<K, V> attrbitues;

	public SerializableAttributes() {
	}

	public SerializableAttributes(Map<K, V> attrbitues) {
		Assert.requiredArgument(attrbitues != null, "attrbitues");
		this.attrbitues = attrbitues;
	}

	public SerializableAttributes(Attributes<K, V> attributes) {
		this();
		Assert.requiredArgument(attrbitues != null, "attrbitues");
		Enumeration<K> enumeration = attributes.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			K key = enumeration.nextElement();
			setAttribute(key, attributes.getAttribute(key));
		}
	}

	@Override
	public V getAttribute(K name) {
		return attrbitues == null ? null : attrbitues.get(name);
	}

	@Override
	public Enumeration<K> getAttributeNames() {
		if (attrbitues == null) {
			return Collections.emptyEnumeration();
		}

		return Collections.enumeration(attrbitues.keySet());
	}

	@Override
	public void setAttribute(K name, V value) {
		if (attrbitues == null) {
			attrbitues = new LinkedHashMap<>(8);
		}
		attrbitues.put(name, value);
	}

	@Override
	public void removeAttribute(K name) {
		if (attrbitues == null) {
			return;
		}
		attrbitues.remove(name);
	}

	public void clear() {
		if (attrbitues != null) {
			this.attrbitues.clear();
		}
	}

	@Override
	public int hashCode() {
		if (attrbitues == null) {
			return 0;
		}
		return attrbitues.hashCode();
	}

	@Override
	public String toString() {
		return attrbitues == null ? "{}" : attrbitues.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof SerializableAttributes) {
			return ObjectUtils.equals(this.attrbitues, ((SerializableAttributes<?, ?>) obj).attrbitues);
		}
		return false;
	}
}
