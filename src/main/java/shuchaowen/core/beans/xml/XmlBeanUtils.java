package shuchaowen.core.beans.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.property.PropertiesFactory;
import shuchaowen.core.exception.BeansException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public final class XmlBeanUtils {
	public static final String PARAMETER_TAG_NAME = "parameter";

	private XmlBeanUtils() {
	};

	public static String formatNodeValue(final PropertiesFactory propertiesFactory, Node node, String value) {
		XmlValue xmlValue = new XmlValue(value, node);
		return xmlValue.formatValue(propertiesFactory);
	}

	public static String getNodeAttributeValue(final PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (value == null || value.length() == 0) {
			return value;
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static String getNodeAttributeValue(Node node, String name) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? null : n.getNodeValue();
	}

	public static String getRequireNodeAttributeValue(Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (StringUtils.isNull(value)) {
			throw new BeansException("not found attribute " + name);
		}
		return value;
	}
	
	public static String getRequireNodeAttributeValue(PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		if (StringUtils.isNull(value)) {
			throw new BeansException("not found attribute " + name);
		}
		return value;
	}

	public static XmlBeanParameter parseBeanParameter(Node node) throws ClassNotFoundException {
		String name = XmlBeanUtils.getNodeAttributeValue(node, "name");
		String ref = XmlBeanUtils.getNodeAttributeValue(node, "ref");
		String value = XmlBeanUtils.getNodeAttributeValue(node, "value");
		String type = XmlBeanUtils.getNodeAttributeValue(node, "type");
		String property = XmlBeanUtils.getNodeAttributeValue(node, "property");

		Class<?> typeClass = StringUtils.isNull(type) ? null : ClassUtils.forName(type);
		if (!StringUtils.isNull(ref)) {
			return new XmlBeanParameter(EParameterType.ref, typeClass, name, ref, node);
		} else if (!StringUtils.isNull(property)) {
			return new XmlBeanParameter(EParameterType.property, typeClass, name, property, node);
		} else {
			if (StringUtils.isNull(value)) {
				value = node.getNodeValue();
			}
			return new XmlBeanParameter(EParameterType.value, typeClass, name, value, node);
		}
	}

	public static boolean isSingleton(Node node) {
		return getBooleanValue(node, "singleton", true);
	}

	public static String[] getNames(Node node) {
		String name = getNodeAttributeValue(node, "name");
		return StringUtils.isNull(name) ? null : StringUtils.commonSplit(name);
	}

	public static String getNodeValue(PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (StringUtils.isNull(value)) {
			value = node.getNodeValue();
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static boolean getBooleanValue(PropertiesFactory propertiesFactory, Node node, String name,
			boolean defaultValue) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public static boolean getBooleanValue(Node node, String name, boolean defaultValue) {
		String value = getNodeAttributeValue(node, name);
		return StringUtils.isNull(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (StringUtils.isNull(getNodeAttributeValue(node, n))) {
				throw new BeansException("not found attribute " + n);
			}
		}
	}

	public static int getIntegerValue(PropertiesFactory propertiesFactory, Node node, String name, int defaultValue) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		return StringUtils.isNull(value) ? defaultValue : Integer.parseInt(value);
	}

	public static List<XmlBeanParameter> parseBeanParameterList(Node node) throws ClassNotFoundException {
		List<XmlBeanParameter> xmlBeanParameters = new ArrayList<XmlBeanParameter>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot.getNodeName().equals(PARAMETER_TAG_NAME)) {
				if (nRoot.getAttributes() == null) {
					continue;
				}

				xmlBeanParameters.add(parseBeanParameter(nRoot));
			}
		}
		return xmlBeanParameters;
	}

	public static Node getRootNode(String config) {
		try {
			File xml = ConfigUtils.getFile(config);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(xml);
			Element root = document.getDocumentElement();
			if (!"beans".equals(root.getTagName())) {
				throw new BeansException("root tag name error [" + root.getTagName() + "]");
			}

			return root;
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}
	
	public static String getPackageName(PropertiesFactory propertiesFactory, Node node){
		return getNodeAttributeValue(propertiesFactory, node, "package");
	}
	
	public static String getVersion(PropertiesFactory propertiesFactory, Node node){
		return getNodeAttributeValue(propertiesFactory, node, "version");
	}
	
	public static String getAddress(PropertiesFactory propertiesFactory, Node node){
		return getRequireNodeAttributeValue(propertiesFactory, node, "address");
	}
	
	public static String getCharsetName(PropertiesFactory propertiesFactory, Node node, String defaultValue){
		String charsetName = getNodeAttributeValue(propertiesFactory, node, "charset");
		return StringUtils.isNull(charsetName)? defaultValue:charsetName;
	}
	
	public static String getCharsetName(Node node, String defaultValue){
		String charsetName = getNodeAttributeValue(node, "charset");
		return StringUtils.isNull(charsetName)? defaultValue:charsetName;
	}
}
