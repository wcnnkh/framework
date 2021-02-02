package scw.dom;

import java.util.Map;

import org.w3c.dom.Node;

import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.support.AbstractMapping;
import scw.util.placeholder.PropertyResolver;
import scw.value.ValueUtils;

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
		String name = this.getDisplayName(field.getSetter());
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
		return ValueUtils.parse(value, field.getSetter().getGenericType());
	}
}
