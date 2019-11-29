package scw.core.attribute;

import java.util.Enumeration;

public class AttributesWrapper<K, V> implements Attributes<K, V>{
	private final Attributes<K, V> targetAttributes;

	public AttributesWrapper(Attributes<K, V> targetAttributes) {
		this.targetAttributes = targetAttributes;
	}

	public V getAttribute(K name) {
		return targetAttributes.getAttribute(name);
	}

	public Enumeration<K> getAttributeNames() {
		return targetAttributes.getAttributeNames();
	}

	public void setAttribute(K name, V o) {
		targetAttributes.setAttribute(name, o);
	}

	public void removeAttribute(K name) {
		targetAttributes.removeAttribute(name);
	}

}
