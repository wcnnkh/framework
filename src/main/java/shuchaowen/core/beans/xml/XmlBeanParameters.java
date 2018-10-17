package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanMethodParameter;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class XmlBeanParameters{
	private static final String PARAMETER_TAG_NAME = "parameter";
	private static final String NAME_KEY = "name";
	private static final String REFERENCE_KEY ="ref";
	private static final String VALUE_KEY = "value";
	private static final String PROPERTY_KEY = "property";
	private static final String TYPE_KEY = "type";
	
	private final List<BeanMethodParameter> list = new ArrayList<BeanMethodParameter>();
	
	public XmlBeanParameters(Node node) throws Exception{
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
				Node typeNode = nRoot.getAttributes().getNamedItem(TYPE_KEY);
				Node propertyNode = nRoot.getAttributes().getNamedItem(PROPERTY_KEY);
				
				String name = nameNode == null? null:nameNode.getNodeValue();
				String ref = refNode == null? null:refNode.getNodeValue();
				String value = valueNode == null? null:valueNode.getNodeValue();
				String type = typeNode == null? null:typeNode.getNodeValue();
				String property = propertyNode == null? null:propertyNode.getNodeValue();
				
				Class<?> typeClass = StringUtils.isNull(type)? null:ClassUtils.forName(type);
				BeanMethodParameter parameter;
				if(!StringUtils.isNull(ref)){
					parameter = new BeanMethodParameter(EParameterType.ref, typeClass, name, ref);
				}else if(!StringUtils.isNull(property)){
					parameter = new BeanMethodParameter(EParameterType.property, typeClass, name, property);
				}else{
					if(StringUtils.isNull(value)){
						value = nRoot.getNodeValue();
					}
					parameter = new BeanMethodParameter(EParameterType.value, typeClass, name, value);
				}
				list.add(parameter);
			}
		}
	}

	public List<BeanMethodParameter> getParameters() {
		return list;
	}
}
