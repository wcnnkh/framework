package shuchaowen.core.beans.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.BeansException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringFormat;
import shuchaowen.core.util.StringUtils;

public final class XmlBeanUtils {
	private static final String NAME_KEY = "name";
	private static final String REFERENCE_KEY = "ref";
	private static final String VALUE_KEY = "value";
	private static final String PROPERTY_KEY = "property";
	private static final String TYPE_KEY = "type";
	private static final String URL_KEY = "url";
	
	public static final String PARAMETER_TAG_NAME = "parameter";
	
	private XmlBeanUtils(){};
	
	private static String formatNodeValue(final PropertiesFactory propertiesFactory, Node node, String value){
		String replacePrefix = getNodeAttributeValue(node, "replace-prefix");
		String replaceSuffix = getNodeAttributeValue(node, "replace-suffix");
		replacePrefix = StringUtils.isNull(replacePrefix)? "{":replacePrefix;
		replaceSuffix = StringUtils.isNull(replaceSuffix)? "}":replaceSuffix;
		StringFormat stringFormat = new StringFormat(replacePrefix, replaceSuffix) {
			
			@Override
			protected String getValue(String key) {
				try {
					return propertiesFactory.getProperties(key, String.class);
				} catch (Exception e) {
					throw new BeansException(e);
				}
			}
		};
		return stringFormat.format(value);
	}
	
	public static String getNodeAttributeValue(final PropertiesFactory propertiesFactory, Node node, String name){
		String value = getNodeAttributeValue(node, name);
		if(value == null || value.length() == 0){
			return value;
		}
	
		return formatNodeValue(propertiesFactory, node, value);
	}
	
	private static String getNodeAttributeValue(Node node, String name){
		NamedNodeMap namedNodeMap = node.getAttributes();
		if(namedNodeMap == null){
			return null;
		}
		
		Node n = namedNodeMap.getNamedItem(name);
		return n == null? null:n.getNodeValue();
	}
	
	public static BeanParameter parseBeanParameter(Node node) throws ClassNotFoundException{
		Node nameNode = node.getAttributes().getNamedItem(NAME_KEY);
		Node refNode = node.getAttributes().getNamedItem(REFERENCE_KEY);
		Node valueNode = node.getAttributes().getNamedItem(VALUE_KEY);
		Node typeNode = node.getAttributes().getNamedItem(TYPE_KEY);
		Node propertyNode = node.getAttributes().getNamedItem(PROPERTY_KEY);
		Node urlNode = node.getAttributes().getNamedItem(URL_KEY);
		
		String name = nameNode == null? null:nameNode.getNodeValue();
		String ref = refNode == null? null:refNode.getNodeValue();
		String value = valueNode == null? null:valueNode.getNodeValue();
		String type = typeNode == null? null:typeNode.getNodeValue();
		String property = propertyNode == null? null:propertyNode.getNodeValue();
		String url = urlNode == null? null:urlNode.getNodeValue();
		
		Map<String, String> attrMap = new HashMap<String, String>();
		NamedNodeMap namedNodeMap = node.getAttributes();
		for(int i=0; i<namedNodeMap.getLength(); i++){
			Node attrNode = namedNodeMap.item(i);
			attrMap.put(attrNode.getNodeName(), attrNode.getNodeValue());
		}
		
		Class<?> typeClass = StringUtils.isNull(type)? null:ClassUtils.forName(type);
		if(!StringUtils.isNull(ref)){
			return new BeanParameter(EParameterType.ref, typeClass, name, ref, attrMap);
		}else if(!StringUtils.isNull(property)){
			return new BeanParameter(EParameterType.property, typeClass, name, property, attrMap);
		}else if(!StringUtils.isNull(url)){
			return new BeanParameter(EParameterType.url, typeClass, name, url, attrMap);
		}else{
			if(StringUtils.isNull(value)){
				value = node.getNodeValue();
			}
			return new BeanParameter(EParameterType.value, typeClass, name, value, attrMap);
		}
	}
	
	public static boolean isSingleton(Node node){
		return getBooleanValue(node, "singleton", true);
	}
	
	public static String[] getNames(Node node){
		String name = getNodeAttributeValue(node, "name");
		return StringUtils.isNull(name)? null:StringUtils.commonSplit(name);
	}
	
	public static String getNodeValue(PropertiesFactory propertiesFactory, Node node, String name){
		String value = getNodeAttributeValue(node, name);
		if(StringUtils.isNull(value)){
			value = node.getNodeValue();
		}
		
		formatNodeValue(propertiesFactory, node, value);
		return value;
	}
	
	public static boolean getBooleanValue(PropertiesFactory propertiesFactory, Node node, String name, boolean defaultValue){
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value)? defaultValue:Boolean.parseBoolean(value);
	}
	
	public static boolean getBooleanValue(Node node, String name, boolean defaultValue){
		String value = getNodeAttributeValue(node, name);
		return StringUtils.isNull(value)? defaultValue:Boolean.parseBoolean(value);
	}
	
	public static void checkAttribute(Node node, String ...name){
		for(String n : name){
			if(StringUtils.isNull(getNodeAttributeValue(node, n))){
				throw new BeansException("not found attribute " + n);
			}
		}
	}
	
	public static int getIntegerValue(PropertiesFactory propertiesFactory, Node node, String name, int defaultValue){
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value)? defaultValue:Integer.parseInt(value);
	}
	
	public static List<BeanParameter> parseBeanParameterList(Node node) throws ClassNotFoundException{
		List<BeanParameter> beanParameters = new ArrayList<BeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals(PARAMETER_TAG_NAME)) {
				if(nRoot.getAttributes() == null){
					continue;
				}
				
				beanParameters.add(parseBeanParameter(nRoot));
			}
		}
		return beanParameters;
	}
	
	public static NodeList getRootNode(String config){
		try {
			File xml = ConfigUtils.getFile(config);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(xml);
			Element root = document.getDocumentElement();
			if (!"beans".equals(root.getTagName())) {
				throw new BeansException("root tag name error [" + root.getTagName() + "]");
			}
			
			return root.getChildNodes();
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}
}
