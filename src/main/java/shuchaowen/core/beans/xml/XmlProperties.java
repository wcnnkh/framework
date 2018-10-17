package shuchaowen.core.beans.xml;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanProperties;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.KeyAlreadyExistsException;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.StringUtils;

public class XmlProperties {
	private static final String FILE_ATTR_KEY = "file";
	private static final String ID_ATTR_KEY = "id";
	private static final String CHARSET_ATTR_KEY = "charset";
	private static final String PREFIX_ATTR_KEY = "prefix";
	
	public static final String PARAMETER_TAG_NAME = "parameter";
	private static final String NAME_KEY = "name";
	private static final String REFERENCE_KEY ="ref";
	private static final String PROPERTY_KEY = "property";
	private static final String VALUE_KEY = "value";
	
	private Properties properties;
	private String id;
	private String charsetName;
	private String prefix;
	private Map<String, BeanProperties> otherPropertiesMap = new HashMap<String, BeanProperties>();
	
	public XmlProperties(Node node){
		if(node.getAttributes() != null){
			Node fileNode = node.getAttributes().getNamedItem(FILE_ATTR_KEY);
			Node idNode = node.getAttributes().getNamedItem(ID_ATTR_KEY);
			Node charsetNode = node.getAttributes().getNamedItem(CHARSET_ATTR_KEY);
			Node prefixNode = node.getAttributes().getNamedItem(PREFIX_ATTR_KEY);
			
			if(charsetNode != null){
				charsetName = charsetNode.getNodeValue();
			}
			
			if(StringUtils.isNull(charsetName)){
				charsetName = Charset.defaultCharset().name();
			}
			
			if(prefixNode != null){
				prefix = prefixNode.getNodeValue();
			}
			
			this.id = idNode == null? null:idNode.getNodeValue();
			String file = fileNode == null? null:fileNode.getNodeValue();
			if(!StringUtils.isNull(file)){
				File f = ConfigUtils.getFile(file);
				properties = ConfigUtils.getProperties(f, charsetName);
				Logger.info("XmlProperties", f.getPath() + ", charset=" + charsetName);
			}
		}
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals(PARAMETER_TAG_NAME)) {
				if(nRoot.getAttributes() == null){
					continue;
				}
				
				Node nameNode = nRoot.getAttributes().getNamedItem(NAME_KEY);
				Node refNode = nRoot.getAttributes().getNamedItem(REFERENCE_KEY);
				Node valueNode = nRoot.getAttributes().getNamedItem(VALUE_KEY);
				Node propertyNode = nRoot.getAttributes().getNamedItem(PROPERTY_KEY);
				
				String name = nameNode == null? null:nameNode.getNodeValue();
				String ref = refNode == null? null:refNode.getNodeValue();
				String value = valueNode == null? null:valueNode.getNodeValue();
				String property = propertyNode == null? null:propertyNode.getNodeValue();
				
				BeanProperties parameter;
				if(!StringUtils.isNull(ref)){
					parameter = new BeanProperties(EParameterType.ref, name, value);
				}if(!StringUtils.isNull(property)){
					parameter = new BeanProperties(EParameterType.property, name, property);
				}else{
					if(StringUtils.isNull(value)){
						value = nRoot.getNodeValue();
					}
					parameter = new BeanProperties(EParameterType.value, name, value);
				}
				
				if(otherPropertiesMap.containsKey(parameter.getName())){
					throw new KeyAlreadyExistsException(parameter.getName());
				}
				
				otherPropertiesMap.put(parameter.getName(), parameter);
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public String getId() {
		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	public Map<String, BeanProperties> getOtherPropertiesMap() {
		return otherPropertiesMap;
	}
	
	public Object getValue(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String name, Class<?> type) throws Exception{
		Object v = null;
		if(properties != null){
			v = properties.get(name);
			if(v != null){
				v = StringUtils.conversion(v.toString(), type);
			}
		}
		
		if(v == null){
			BeanProperties beanProperties = otherPropertiesMap.get(name);
			if(beanProperties != null){
				switch (beanProperties.getType()) {
				case value:
					v = StringUtils.conversion(beanProperties.getValue(), type);
					break;
				case ref:
					v = beanFactory.get(beanProperties.getValue());
				case property:
					v = propertiesFactory.getProperties(name, type);
				default:
					break;
				}
			}
		}
		return v;
	}
}
	
