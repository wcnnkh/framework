package io.basc.framework.dom;

import io.basc.framework.util.placeholder.PlaceholderFormat;

import org.w3c.dom.Node;

public class DomAttributeMapping extends DomMapping {

	public DomAttributeMapping(PlaceholderFormat placeholderFormat, Node node) {
		super(placeholderFormat, DomUtils.attributeAsMap(node));
	}
}
