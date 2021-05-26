package scw.dom;

import org.w3c.dom.Node;

import scw.util.placeholder.PropertyResolver;

public class DomAttributeMapping extends DomMapping {

	public DomAttributeMapping(PropertyResolver propertyResolver, Node node) {
		super(propertyResolver, DomUtils.attributeAsMap(node));
	}
}
