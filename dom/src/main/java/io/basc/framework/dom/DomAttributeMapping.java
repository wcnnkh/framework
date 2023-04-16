package io.basc.framework.dom;

import org.w3c.dom.Node;

import io.basc.framework.env.Environment;

public class DomAttributeMapping extends DomMapping {

	public DomAttributeMapping(Environment environment, Node node) {
		super(environment, DomUtils.attributeAsMap(node));
	}
}
