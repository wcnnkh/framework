package scw.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.common.exception.BeansException;
import scw.common.utils.ConfigUtils;
import scw.common.utils.StringUtils;

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
		String value;
		Property property = propertiesMap.get(key);
		if(property == null){
			value = ConfigUtils.getSystemProperty(key);
		}else{
			value = property.getXmlValue().formatValue(this);
		}
		
		if(value == null){
			throw new BeansException("not found property:" + key);
		}
		return value;
	}
}
