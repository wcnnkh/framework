package scw.xml;

import java.util.Map;

import org.w3c.dom.Node;

import scw.mapper.Field;
import scw.mapper.support.AbstractMapping;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public class XmlMapping extends AbstractMapping {
	private final PropertyFactory propertyFactory;
	private final Map<String, Node> nodeMap;

	public XmlMapping(PropertyFactory propertyFactory, Map<String, Node> nodeMap) {
		this.propertyFactory = propertyFactory;
		this.nodeMap = nodeMap;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
	
	@Override
	protected boolean isNesting(Field field) {
		return false;
	}

	@Override
	protected Object getValue(Field field) {
		String name = this.getDisplayName(field.getSetter());
		Node node = nodeMap.get(name);
		if (node == null) {
			return null;
		}

		String value = XMLUtils.formatNodeValue(propertyFactory, node, node.getNodeValue());
		if (value == null) {
			return null;
		}

		return getNodeValue(name, value, field.getSetter().getType(), field, node);
	}

	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		return ValueUtils.parse(value, field.getSetter().getGenericType());
	}
}
