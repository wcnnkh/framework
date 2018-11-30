package shuchaowen.core.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.xml.XmlBeanUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class XmlPropertiesFactory implements PropertiesFactory {
	private static final String PROPERTIES_TAG_NAME = "properties";
	private Map<String, Property> propertiesMap = new HashMap<String, Property>();

	public XmlPropertiesFactory(String beanXml) {
		if (StringUtils.isNull(beanXml)) {
			return;
		}
		
		NodeList nhosts = XmlBeanUtils.getRootNode(beanXml).getChildNodes();
		for (int i = 0; i < nhosts.getLength(); i++) {
			Node nRoot = nhosts.item(i);
			if (PROPERTIES_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				Map<String, Property> map = XmlPropertyUtils.parse(nRoot);
				if(map != null){
					propertiesMap.putAll(map);
				}
			}
		}

	}

	public String getValue(String key) {
		Property property = propertiesMap.get(key);
		if(property == null){
			return ConfigUtils.getSystemProperty(key);
		}else{
			return property.getXmlValue().formatValue(this);
		}
	}
}
