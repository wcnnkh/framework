package scw.xml;

import org.w3c.dom.Node;

import scw.util.value.property.PropertyFactory;

public class XmlAttributeMapper extends XmlMapper {

	public XmlAttributeMapper(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, XMLUtils.attributeAsMap(node));
	}
}
