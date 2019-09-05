package scw.mvc;

import java.util.Enumeration;

public class AttributeManagerWrapper implements AttributeManager {
	private final AttributeManager targetAttributeManager;

	public AttributeManagerWrapper(AttributeManager attributeManager) {
		this.targetAttributeManager = attributeManager;
	}

	public final AttributeManager getTargetAttributeManager() {
		return targetAttributeManager;
	}

	public Object getAttribute(String name) {
		return targetAttributeManager.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return targetAttributeManager.getAttributeNames();
	}

	public void setAttribute(String name, Object o) {
		targetAttributeManager.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		targetAttributeManager.removeAttribute(name);
	}

}
