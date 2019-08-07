package scw.beans.property;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.utils.ResourceUtils;
import scw.core.utils.SystemPropertyUtils;

public class XmlPropertyFactory implements PropertyFactory {
	private Map<String, String> propertyMap = new HashMap<String, String>();

	public XmlPropertyFactory(String beanXml) {
		init(beanXml);
	}

	private void init(String xml) {
		if (!ResourceUtils.isExist(xml)) {
			return;
		}

		Map<String, Property> propertiesMap = new LinkedHashMap<String, Property>();
		NodeList nhosts = XmlBeanUtils.getRootNodeList(xml);
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if ("properties".equalsIgnoreCase(nRoot.getNodeName())) {
				Map<String, Property> map = XmlPropertyUtils.parse(nRoot);
				if (map != null) {
					for (Entry<String, Property> entry : map.entrySet()) {
						if (propertiesMap.containsKey(entry.getKey())) {
							throw new AlreadyExistsException(entry.getKey());
						}
						propertiesMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		for (Entry<String, Property> entry : propertiesMap.entrySet()) {
			String value = entry.getValue().getXmlValue().formatValue(this);
			if (entry.getValue().isSystem()) {
				System.setProperty(entry.getKey(), value);
			} else {
				propertyMap.put(entry.getKey(), value);
			}
		}
	}

	public String getProperty(String key) {
		String value = propertyMap.get(key);
		if (value == null) {
			value = SystemPropertyUtils.getProperty(key);
		}
		return value;
	}
}
