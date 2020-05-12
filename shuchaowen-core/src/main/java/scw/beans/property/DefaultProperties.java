package scw.beans.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import scw.core.utils.StringUtils;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public final class DefaultProperties extends AbstractProperties {

	public DefaultProperties(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
	}

	public Map<String, Property> getPropertyMap() {
		Map<String, Property> map = new HashMap<String, Property>();
		java.util.Properties properties = XmlPropertyUtils.getProperties(getNode());
		if (properties != null) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Property property = new PropertiesPropertyValue(getPropertyFactory(), getNode(),
						entry.getKey().toString(), entry.getValue().toString());
				map.put(property.getName(), property);
			}
		}

		Properties simpleProperties = new SimpleProperties(getPropertyFactory(), getNode());
		map.putAll(simpleProperties.getPropertyMap());
		return map;
	}

	private static class PropertiesPropertyValue extends AbstractProperty {
		private String name;
		private String value;

		public PropertiesPropertyValue(PropertyFactory propertyFactory, Node node, String name, String value) {
			super(propertyFactory, node);
			String prefix = XmlPropertyUtils.getPrefix(node);
			this.name = StringUtils.isEmpty(prefix) ? name : (prefix + name);
			this.value = value;
		}

		public String getValue() {
			return XMLUtils.formatNodeValue(getPropertyFactory(), getNode(), value);
		}

		public String getName() {
			return name;
		}
		
		
	}

}
