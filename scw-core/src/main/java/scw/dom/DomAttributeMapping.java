package scw.dom;

import org.w3c.dom.Node;

import scw.value.property.PropertyFactory;

public class DomAttributeMapping extends DomMapping {

	public DomAttributeMapping(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, DomUtils.attributeAsMap(node));
	}
}
