package scw.xml;

import java.util.Map;

import org.w3c.dom.Node;

import scw.mapper.AbstractMapper;
import scw.mapper.FieldContext;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public class XmlMapper extends AbstractMapper {
	private final PropertyFactory propertyFactory;
	private final Map<String, Node> nodeMap;

	public XmlMapper(PropertyFactory propertyFactory, Map<String, Node> nodeMap) {
		this.propertyFactory = propertyFactory;
		this.nodeMap = nodeMap;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
	
	@Override
	protected boolean isNesting(FieldContext fieldContext) {
		return false;
	}

	@Override
	protected Object getValue(FieldContext fieldContext) {
		String name = this.getDisplayName(fieldContext.getField().getSetter());
		Node node = nodeMap.get(name);
		if (node == null) {
			return null;
		}

		String value = XMLUtils.formatNodeValue(propertyFactory, node, node.getNodeValue());
		if (value == null) {
			return null;
		}

		return getNodeValue(name, value, fieldContext.getField().getSetter().getType(), fieldContext, node);
	}

	protected Object getNodeValue(String name, String value, Class<?> type, FieldContext fieldContext, Node node) {
		return ValueUtils.parse(value, fieldContext.getField().getSetter().getGenericType());
	}
}
