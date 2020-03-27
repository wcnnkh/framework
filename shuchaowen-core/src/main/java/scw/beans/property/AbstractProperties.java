package scw.beans.property;

import org.w3c.dom.Node;

import scw.util.value.property.PropertyFactory;

public abstract class AbstractProperties implements Properties {
	private final PropertyFactory propertyFactory;
	private final Node node;

	public AbstractProperties(PropertyFactory propertyFactory, Node node) {
		this.propertyFactory = propertyFactory;
		this.node = node;
	}

	public final PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public final Node getNode() {
		return node;
	}

	public long getRefreshPeriod() {
		return XmlPropertyUtils.getRefreshPeriod(node);
	}

}
