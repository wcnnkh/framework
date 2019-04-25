package scw.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.exception.NotFoundException;

public final class XMLUtils {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

	static {
		DOCUMENT_BUILDER_FACTORY.setIgnoringElementContentWhitespace(true);
		DOCUMENT_BUILDER_FACTORY.setIgnoringComments(true);
		DOCUMENT_BUILDER_FACTORY.setCoalescing(true);
		DOCUMENT_BUILDER_FACTORY.setExpandEntityReferences(false);
	}

	private XMLUtils() {
	}

	public static DocumentBuilder newDocumentBuilder() {
		try {
			return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static Transformer newTransformer() {
		try {
			return TRANSFORMER_FACTORY.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static Document parse(InputStream is) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		try {
			return documentBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document parse(File file) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		try {
			return documentBuilder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document parse(InputSource is) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		try {
			return documentBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document parseForURI(String uri) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		try {
			return documentBuilder.parse(uri);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document parse(InputStream is, String systemId) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		try {
			return documentBuilder.parse(is, systemId);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document parse(String text) {
		return parse(new InputSource(new StringReader(text)));
	}

	public static Element getRootElement(String xmlPath) {
		File xml = ConfigUtils.getFile(xmlPath);
		Document document = XMLUtils.parse(xml);
		return document.getDocumentElement();
	}

	public static String asXml(Element element) {
		Transformer transformer;
		try {
			transformer = TRANSFORMER_FACTORY.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource domSource = new DOMSource(element);
		String content = null;
		try {
			transformer.transform(domSource, result);
			content = sw.toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			XUtils.close(sw);
		}
		return content;
	}

	public static LinkedHashMap<String, String> xmlToMap(Node node) {
		NodeList nodeList = node.getChildNodes();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n == null) {
				continue;
			}

			String nodeName = n.getNodeName();
			if (!checkNodeName(nodeName)) {
				continue;
			}

			String value = n.getTextContent();
			map.put(nodeName, value);
		}
		return map;
	}

	public static LinkedHashMap<String, String> xmlToMap(String text) {
		Document document = parse(text);
		return xmlToMap(document.getDocumentElement());
	}

	public static Element createElement(Map<String, String> map, String rootName) {
		Document document = newDocumentBuilder().newDocument();
		Element root = document.createElement(rootName);
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			Element element = document.createElement(entry.getKey());
			element.setTextContent(entry.getValue());
			root.appendChild(element);
		}
		return root;
	}

	public static String getNodeAttributeValue(Node node, String name) {
		return getNodeAttributeValue(node, name, null);
	}

	/**
	 * @param basicType
	 *            只能是基本数据类型，非基本数据类型只能是String
	 * @param node
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static <T> T getNodeAttributeValue(Class<T> basicType, Node node, String name, T defaultValue) {
		String value = getNodeAttributeValue(node, name);
		if (value == null) {
			return defaultValue;
		} else {
			return StringParseUtils.conversion(value, basicType);
		}
	}

	public static String getNodeAttributeValue(Node node, String name, String defaultValue) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? defaultValue : n.getNodeValue();
	}

	public static String getNodeAttributeValueOrNodeContent(Node node, String name) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? node.getTextContent() : n.getNodeValue();
	}

	public static String getRequireNodeAttributeValue(Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (StringUtils.isNull(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return value;
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (StringUtils.isNull(XMLUtils.getNodeAttributeValue(node, n))) {
				throw new NotFoundException("not found attribute " + n);
			}
		}
	}

	public static boolean getBooleanValue(Node node, String name, boolean defaultValue) {
		String value = getNodeAttributeValue(node, name);
		return StringUtils.isNull(value) ? defaultValue : Boolean.parseBoolean(value);
	}

	public static <T> T getBean(Node node, Class<T> type) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T t = null;
		ClassInfo classInfo = ClassUtils.getClassInfo(type.getName());
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			String nodeName = n.getNodeName();
			if (!checkNodeName(nodeName)) {
				continue;
			}

			FieldInfo fieldInfo = classInfo.getFieldInfo(nodeName);
			if (fieldInfo == null) {
				continue;
			}

			if (fieldInfo.isStatic() || fieldInfo.isFinal()) {
				continue;
			}

			String value = n.getTextContent();
			if (value == null) {
				continue;
			}

			if (t == null) {
				t = ClassUtils.newInstance(type);
			}
			
			fieldInfo.set(t, StringParseUtils.conversion(value, fieldInfo.getType()));
		}
		return t;
	}

	public static <T> List<T> getBeanList(Node rootNode, Class<T> type)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (rootNode == null) {
			return null;
		}

		NodeList nodeList = rootNode.getChildNodes();
		if (nodeList == null) {
			return null;
		}

		List<T> list = new ArrayList<T>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String nodeName = node.getNodeName();
			if (!checkNodeName(nodeName)) {
				continue;
			}

			T t;
			try {
				t = getBean(node, type);
				if (t == null) {
					continue;
				}
				list.add(t);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static boolean checkNodeName(String name) {
		return !(name == null || name.length() == 0 || "#text".equals(name));
	}
	
	public static Map<String, String> attributeAsMap(Node node){
		if(node == null){
			return null;
		}

		NamedNodeMap map = node.getAttributes();
		if(map == null){
			return null;
		}

		int size = map.getLength();
		Map<String, String> properties = new HashMap<String, String>(size, 1);
		for(int i=0; i<size; i++){
			Node n = map.item(i);
			properties.put(n.getNodeName(), n.getNodeValue());
		}
		return properties;
	}
}
