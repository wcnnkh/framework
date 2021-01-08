package scw.dom;

import java.util.Map;

import org.w3c.dom.Node;

import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.support.AbstractMapping;
import scw.value.ValueUtils;
import scw.value.property.PropertyFactory;

public class DomMapping extends AbstractMapping {
	private final PropertyFactory propertyFactory;
	private final Map<String, Node> nodeMap;

	public DomMapping(PropertyFactory propertyFactory, Map<String, Node> nodeMap) {
		this.propertyFactory = propertyFactory;
		this.nodeMap = nodeMap;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
	
	@Override
	protected boolean isNesting(FieldDescriptor fieldDescriptor) {
		return false;
	}

	@Override
	protected Object getValue(Field field) {
		String name = this.getDisplayName(field.getSetter());
		Node node = nodeMap.get(name);
		if (node == null) {
			return null;
		}

		String value = DomUtils.formatNodeValue(propertyFactory, node, node.getNodeValue());
		if (value == null) {
			return null;
		}

		return getNodeValue(name, value, field.getSetter().getType(), field, node);
	}

	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		return ValueUtils.parse(value, field.getSetter().getGenericType());
	}
}
