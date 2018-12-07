package shuchaowen.beans.property;

import org.w3c.dom.Node;

import shuchaowen.beans.xml.XmlBeanUtils;
import shuchaowen.beans.xml.XmlValue;

public class Property {
	private final String name;
	private final XmlValue xmlValue;
	
	public Property(Node node, String parentCharsetName){
		this.name = XmlBeanUtils.getRequireNodeAttributeValue(node, "name");
		this.xmlValue = new XmlValue(node, parentCharsetName);
	}
	
	public Property(String name, String value, Node node){
		this.name = name;
		this.xmlValue = new XmlValue(value, node);
	}
	
	public String getName() {
		return name;
	}

	public XmlValue getXmlValue() {
		return xmlValue;
	}
}
