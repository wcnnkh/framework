package scw.beans.property;

import org.w3c.dom.Node;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;

public abstract class AbstractProperty implements Property {
	private final PropertyFactory propertyFactory;
	private final Node node;

	public AbstractProperty(PropertyFactory propertyFactory, Node node) {
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
		String prefix = null;
		Node p = node.getParentNode();
		if (p != null) {
			prefix = XmlPropertyUtils.getPrefix(p);
		}

		String name = XmlPropertyUtils.getName(node);
		return StringUtils.isEmpty(prefix) ? name : (prefix + name);
	}

	public final boolean isSystem() {
		return XmlPropertyUtils.isSystem(node);
	}

	public boolean isRefresh() {
		return XmlPropertyUtils.isRefresh(node, true);
	}
}
