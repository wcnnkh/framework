package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanProperties;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.util.StringUtils;

public class XmlBeanProperties {
	public static final String PARAMETER_TAG_NAME = "parameter";
	private static final String NAME_KEY = "name";
	private static final String REFERENCE_KEY ="ref";
	private static final String PROPERTY_KEY = "property";
	private static final String VALUE_KEY = "value";
	
	private final List<BeanProperties> list = new ArrayList<BeanProperties>();
	
	public XmlBeanProperties(Node node){
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
				list.add(parameter);
			}
		}
	}

	public List<BeanProperties> getProperties() {
		return list;
	}
}
