package scw.beans.property;

import org.w3c.dom.Node;

import scw.beans.xml.XmlValue;
import scw.core.utils.XMLUtils;

public class Property {
	private final String name;
	private final XmlValue xmlValue;
	private final boolean system;

	public Property(Node node, String parentCharsetName, Boolean parentSystem) {
		this.name = XMLUtils.getRequireNodeAttributeValue(node, "name");
		this.xmlValue = new XmlValue(node, parentCharsetName);
		Boolean system = XmlPropertyUtils.isSystem(node);
		if (system == null) {
			this.system = parentSystem == null ? false : parentSystem;
		} else {
			this.system = system;
		}
	}

	public Property(String name, String value, Node node, boolean system) {
		this.name = name;
		this.xmlValue = new XmlValue(value, node);
		this.system = system;
	}

	public String getName() {
		return name;
	}

	public XmlValue getXmlValue() {
		return xmlValue;
	}

	public boolean isSystem() {
		return system;
	}
}
