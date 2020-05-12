package scw.beans.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.lang.AlreadyExistsException;
import scw.value.property.StringValuePropertyFactory;

public class SimplePropertyFactory extends StringValuePropertyFactory {
	private Map<String, Property> propertyMap = new HashMap<String, Property>();
	private ArrayList<Properties> propertiesList = new ArrayList<Properties>();

	public SimplePropertyFactory(NodeList nodeList) {
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

			Map<String, Property> map = properties.getPropertyMap();
			for (Entry<String, Property> entry : map.entrySet()) {
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

	@Override
	protected String getStringValue(String key) {
		Property property = propertyMap.get(key);
		return property == null ? null : property.getValue();
	}

	@Override
	protected Enumeration<String> internalEnumerationKeys() {
		return Collections.enumeration(propertyMap.keySet());
	}
}
