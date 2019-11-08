package scw.core.attribute;

import java.util.Enumeration;

public class AttributesWrapper<T> implements Attributes<T>{
	private final Attributes<T> targetAttributes;

	public AttributesWrapper(Attributes<T> targetAttributes) {
		this.targetAttributes = targetAttributes;
	}

	public T getAttribute(String name) {
		return targetAttributes.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return targetAttributes.getAttributeNames();
	}

	public void setAttribute(String name, T o) {
		targetAttributes.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		targetAttributes.removeAttribute(name);
	}

}
