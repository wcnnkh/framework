package shuchaowen.core.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.xml.XmlBeanUtils;
import shuchaowen.core.beans.xml.XmlProperties;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class XmlPropertiesFactory implements PropertiesFactory2{
	private static final String PROPERTIES_TAG_NAME = "properties";
	private Map<String, String> propertiesMap;
	
	public XmlPropertiesFactory(String beanXml){
		if (StringUtils.isNull(beanXml)) {
			return ;
		}

			NodeList nhosts = XmlBeanUtils.getRootNode(beanXml);
			List<XmlProperties> xmlPropertiesList = new ArrayList<XmlProperties>();
			for (int i = 0; i < nhosts.getLength(); i++) {
				Node nRoot = nhosts.item(i);
				if (PROPERTIES_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
				}
			}

	}
	
	public String getValue(String key) {
		String value = propertiesMap.get(key);
		if(value == null){
			value = ConfigUtils.getSystemProperty(key);
		}
		return value;
	}
}
