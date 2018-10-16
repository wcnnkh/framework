package shuchaowen.core.beans.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.BeanProperties;
import shuchaowen.core.beans.TParameterType;
import shuchaowen.core.util.StringUtils;

public class XmlBeanProperties {
	private static final String PARAMETER_TAG_NAME = "parameter";
	private static final String NAME_KEY = "name";
	private static final String REFERENCE_KEY ="ref";
	private static final String CONFIG_KEY = "config";
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
				Node configNode = nRoot.getAttributes().getNamedItem(CONFIG_KEY);
				
				String name = nameNode == null? null:nameNode.getNodeValue();
				String ref = refNode == null? null:refNode.getNodeValue();
				String value = valueNode == null? null:valueNode.getNodeValue();
				String config = configNode == null? null:configNode.getNodeValue();
				
				BeanProperties parameter;
				if(!StringUtils.isNull(ref)){
					parameter = new BeanProperties(TParameterType.ref, name, value);
				}else if(!StringUtils.isNull(config)){
					parameter = new BeanProperties(TParameterType.config, name, value);
				}else{
					parameter = new BeanProperties(TParameterType.value, name, value);
				}
				list.add(parameter);
			}
		}
	}

	public List<BeanProperties> getProperties() {
		return list;
	}
}
