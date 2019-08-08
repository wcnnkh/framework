package scw.beans.property;

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
	private final Map<String, Property> propertyMap = new LinkedHashMap<String, Property>();

	public XmlPropertyFactory(String beanXml) {
		init(beanXml);
	}

	private void init(String xml) {
		if (!ResourceUtils.isExist(xml)) {
			return;
		}

		NodeList nhosts = XmlBeanUtils.getRootNodeList(xml);
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if ("properties".equalsIgnoreCase(nRoot.getNodeName())) {
				Map<String, Property> map = XmlPropertyUtils.parse(nRoot);
				if (map != null) {
					for (Entry<String, Property> entry : map.entrySet()) {
						if (propertyMap.containsKey(entry.getKey())) {
							throw new AlreadyExistsException(entry.getKey());
						}
						propertyMap.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
	}

	public String getProperty(String key) {
		Property property = propertyMap.get(key);
		return property == null ? SystemPropertyUtils.getProperty(key) : property.getXmlValue().formatValue(this);
	}
}
