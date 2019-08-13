package scw.beans.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class DefaultProperties extends AbstractProperties {

	public DefaultProperties(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
	}

	public Map<String, PropertyValue> getPropertyMap() {
		Map<String, PropertyValue> map = new HashMap<String, PropertyValue>();
		java.util.Properties properties = XmlPropertyUtils
				.getProperties(getNode());
		if (properties != null) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				PropertyValue propertyValue = new PropertiesPropertyValue(
						getPropertyFactory(), getNode(), key, value);
				map.put(key, propertyValue);
			}
		}

		Properties simpleProperties = new SimpleProperties(
				getPropertyFactory(), getNode());
		map.putAll(simpleProperties.getPropertyMap());
		return map;
	}

	private static class PropertiesPropertyValue extends AbstractPropertyValue {
		private String name;
		private String value;

		public PropertiesPropertyValue(PropertyFactory propertyFactory,
				Node node, String name, String value) {
			super(propertyFactory, node);
			String prefix = XmlPropertyUtils.getPrefix(node);
			this.name = StringUtils.isEmpty(prefix) ? name : (prefix + name);
			this.value = value;
		}

		public String getValue() {
			return XMLUtils.formatNodeValue(getPropertyFactory(), getNode(),
					value);
		}

		public String getName() {
			return name;
		}
	}

}
