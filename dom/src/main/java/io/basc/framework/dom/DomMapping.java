package io.basc.framework.dom;

import io.basc.framework.mapper.AbstractMapping;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.util.placeholder.PropertyResolver;
import io.basc.framework.value.StringValue;

import java.util.Map;

import org.w3c.dom.Node;

public class DomMapping extends AbstractMapping {
	private final PropertyResolver propertyResolver;
	private final Map<String, Node> nodeMap;

	public DomMapping(PropertyResolver propertyResolver, Map<String, Node> nodeMap) {
		this.propertyResolver = propertyResolver;
		this.nodeMap = nodeMap;
	}

	@Override
	protected boolean isNesting(FieldDescriptor fieldDescriptor) {
		return false;
	}

	@Override
	protected Object getValue(Field field) {
		String name = field.getSetter().getName();
		Node node = nodeMap.get(name);
		if (node == null) {
			return null;
		}

		String value = DomUtils.formatNodeValue(propertyResolver, node, node.getNodeValue());
		if (value == null) {
			return null;
		}

		return getNodeValue(name, value, field.getSetter().getType(), field, node);
	}

	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		return StringValue.parse(value, field.getSetter().getGenericType());
	}
}
