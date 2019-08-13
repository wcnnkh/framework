package scw.beans.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.PropertyFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.utils.SystemPropertyUtils;

public class XmlPropertyFactory2 implements PropertyFactory {
	private Map<String, PropertyValue> propertyMap = new HashMap<String, PropertyValue>();
	private ArrayList<Properties> propertiesList = new ArrayList<Properties>();

	public XmlPropertyFactory2(NodeList nodeList) {
		if (nodeList == null || nodeList.getLength() == 0) {
			return;
		}

		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!"properties".equalsIgnoreCase(node.getNodeName())) {
				continue;
			}

			Properties properties = new DefaultProperties(this, node);
			propertiesList.add(properties);

			Map<String, PropertyValue> map = properties.getPropertyMap();
			for (Entry<String, PropertyValue> entry : map.entrySet()) {
				if (propertyMap.containsKey(entry.getKey())) {
					throw new AlreadyExistsException(entry.getKey());
				}

				propertyMap.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public List<Properties> getPropertiesList() {
		return propertiesList;
	}

	public String getProperty(String key) {
		PropertyValue propertyValue = propertyMap.get(key);
		return propertyValue == null ? SystemPropertyUtils.getProperty(key)
				: propertyValue.getValue();
	}
}
