package io.basc.framework.dom;

import java.util.Map;

import org.w3c.dom.Node;

import io.basc.framework.mapper.AbstractMapping;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.value.Value;

public class DomMapping extends AbstractMapping {
	private final PlaceholderFormat placeholderFormat;
	private final Map<String, Node> nodeMap;

	public DomMapping(PlaceholderFormat placeholderFormat, Map<String, Node> nodeMap) {
		this.placeholderFormat = placeholderFormat;
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

		String value = DomUtils.formatNodeValue(placeholderFormat, node, node.getNodeValue());
		if (value == null) {
			return null;
		}

		return getNodeValue(name, value, field.getSetter().getType(), field, node);
	}

	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		return Value.of(value).getAsObject(field.getSetter().getGenericType());
	}
}
