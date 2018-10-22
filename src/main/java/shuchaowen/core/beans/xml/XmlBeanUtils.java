package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.util.ClassUtils;
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
	
	public static List<BeanParameter> parseBeanParameterList(Node node) throws ClassNotFoundException{
		List<BeanParameter> beanParameters = new ArrayList<BeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals(PARAMETER_TAG_NAME)) {
				if(nRoot.getAttributes() == null){
					continue;
				}
				
				beanParameters.add(XmlBeanUtils.parseBeanParameter(nRoot));
			}
		}
		return beanParameters;
	}
}
