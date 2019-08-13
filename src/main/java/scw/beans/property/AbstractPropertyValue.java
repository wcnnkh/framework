package scw.beans.property;

import org.w3c.dom.Node;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;

public abstract class AbstractPropertyValue implements PropertyValue {
	private final PropertyFactory propertyFactory;
	private final Node node;

	public AbstractPropertyValue(PropertyFactory propertyFactory, Node node) {
		this.propertyFactory = propertyFactory;
		this.node = node;
	}

	public final PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public final Node getNode() {
		return node;
	}

	public String getName() {
		String prefix = XmlPropertyUtils.getPrefix(node);
		String name = XmlPropertyUtils.getName(node);
		return StringUtils.isEmpty(prefix) ? name : (prefix + name);
	}

	public final boolean isSystem() {
		return XmlPropertyUtils.isSystem(node);
	}
}
