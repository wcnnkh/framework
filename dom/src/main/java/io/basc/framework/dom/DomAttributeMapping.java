package io.basc.framework.dom;

import io.basc.framework.util.placeholder.PropertyResolver;

import org.w3c.dom.Node;

public class DomAttributeMapping extends DomMapping {

	public DomAttributeMapping(PropertyResolver propertyResolver, Node node) {
		super(propertyResolver, DomUtils.attributeAsMap(node));
	}
}
