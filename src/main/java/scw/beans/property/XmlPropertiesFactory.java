package scw.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.SystemPropertyUtils;

public class XmlPropertiesFactory implements PropertiesFactory {
	private Map<String, Property> propertiesMap = new HashMap<String, Property>();

	public XmlPropertiesFactory(String beanXml) {
		if (!ResourceUtils.isExist(beanXml)) {
			return;
		}

		NodeList nhosts = XmlBeanUtils.getRootNodeList(beanXml);
		;
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if ("properties".equalsIgnoreCase(nRoot.getNodeName())) {
				Map<String, Property> map = XmlPropertyUtils.parse(nRoot);
				if (map != null) {
					propertiesMap.putAll(map);
				}
			}
		}
	}

	public String getValue(String key) {
		String value;
		Property property = propertiesMap.get(key);
		if (property == null) {
			value = SystemPropertyUtils.getProperty(key);
		} else {
			value = property.getXmlValue().formatValue(this);
		}
		return value;
	}
}
