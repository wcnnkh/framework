package shuchaowen.core.beans.xml;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.StringUtils;

public class XmlProperties {
	private static final String FILE_ATTR_KEY = "file";
	private static final String ID_ATTR_KEY = "id";
	private static final String CHARSET_ATTR_KEY = "charset";
	private static final String PREFIX_ATTR_KEY = "prefix";
	
	public static final String PARAMETER_TAG_NAME = "parameter";
	
	private Properties properties;
	private String id;
	private String charsetName;
	private String prefix;
	private Map<String, BeanParameter> otherPropertiesMap = new HashMap<String, BeanParameter>();
	private Map<String, String> attrMap = new HashMap<String, String>();
	
	public XmlProperties(Node node) throws ClassNotFoundException{
		if(node.getAttributes() != null){
			NamedNodeMap namedNodeMap = node.getAttributes();
			for(int i=0; i<namedNodeMap.getLength(); i++){
				Node attrNode = namedNodeMap.item(i);
				attrMap.put(attrNode.getNodeName(), attrNode.getNodeValue());
			}
			
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
		
		for(BeanParameter beanParameter : XmlBeanUtils.parseBeanParameterList(node)){
			if(otherPropertiesMap.containsKey(beanParameter.getName())){
				throw new AlreadyExistsException(beanParameter.getName());
			}
			
			otherPropertiesMap.put(beanParameter.getName(), beanParameter);
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

	public Map<String, BeanParameter> getOtherPropertiesMap() {
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
			BeanParameter beanProperties = otherPropertiesMap.get(name).clone();
			beanProperties.setParameterType(type);
			v = beanProperties.parseValue(beanFactory, propertiesFactory);
		}
		return v;
	}

	public Map<String, String> getAttrMap() {
		return attrMap;
	}
}
	
