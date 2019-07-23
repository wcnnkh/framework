package scw.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import scw.core.PropertiesFactory;
import scw.core.StringFormat;
import scw.core.exception.NotFoundException;
import scw.core.reflect.PropertyMapper;
import scw.core.reflect.ReflectUtils;
import scw.io.IOUtils;

public final class XMLUtils {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory
			.newInstance();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory
			.newInstance();

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

	private static MyNodeList getIncludeNodeList(
			HashSet<String> includeHashSet, Node includeNode) {
		String file = getNodeAttributeValue(includeNode, "file");
		if (StringUtils.isEmpty(file)) {
			return new MyNodeList();
		}

		File xml = ConfigUtils.getFile(file);
		if (!xml.exists()) {
			return new MyNodeList();
		}

		if (includeHashSet.contains(xml.getPath())) {
			throw new RuntimeException(file + "存在循环引用，请检查include地址");
		}

		includeHashSet.add(xml.getPath());
		Document document = XMLUtils.parse(xml);
		Node root = document.getDocumentElement();
		if (root == null) {
			return new MyNodeList();
		}

		NodeList nodeList = root.getChildNodes();
		MyNodeList list = new MyNodeList();
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node n = nodeList.item(i);
			if (n == null) {
				continue;
			}

			list.add(n);
		}
		return list;
	}

	private static MyNodeList converIncludeNodeList(NodeList nodeList,
			HashSet<String> includeHashSet) {
		MyNodeList list = new MyNodeList();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (n.getNodeName().startsWith("include")) {
					MyNodeList n2 = getIncludeNodeList(includeHashSet, n);
					list.addAll(converIncludeNodeList(n2, includeHashSet));
				} else {
					list.add(n);
				}
			}
		}
		return list;
	}

	public static NodeList getChildNodes(Node node, boolean include) {
		return include ? converIncludeNodeList(node.getChildNodes(),
				new HashSet<String>()) : node.getChildNodes();
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
			IOUtils.close(sw);
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
	public static <T> T getNodeAttributeValue(Class<T> basicType, Node node,
			String name, T defaultValue) {
		String value = getNodeAttributeValue(node, name);
		if (value == null) {
			return defaultValue;
		} else {
			return StringUtils.conversion(value, basicType);
		}
	}

	public static String getNodeAttributeValue(Node node, String name,
			String defaultValue) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? defaultValue : n.getNodeValue();
	}

	public static String getNodeAttributeValueOrNodeContent(Node node,
			String name) {
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

	public static boolean getBooleanValue(Node node, String name,
			boolean defaultValue) {
		String value = getNodeAttributeValue(node, name);
		return StringUtils.isNull(value) ? defaultValue : Boolean
				.parseBoolean(value);
	}

	public static <T> T getBean(Node node, Class<T> type) throws Exception {
		T t = null;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			String nodeName = n.getNodeName();
			if (!checkNodeName(nodeName)) {
				continue;
			}

			Field field = ReflectUtils.getField(type, nodeName, true);
			if (field == null) {
				continue;
			}

			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			String value = n.getTextContent();
			if (value == null) {
				continue;
			}

			if (t == null) {
				t = ReflectUtils.newInstance(type);
			}

			ReflectUtils.setFieldValue(type, field, t,
					StringUtils.conversion(value, field.getType()));
		}
		return t;
	}

	public static <T> List<T> getBeanList(Node rootNode, Class<T> type)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static boolean checkNodeName(String name) {
		return !(name == null || name.length() == 0 || "#text".equals(name));
	}

	public static Map<String, Node> attributeAsMap(Node node) {
		if (node == null) {
			return null;
		}

		NamedNodeMap map = node.getAttributes();
		if (map == null) {
			return null;
		}

		int size = map.getLength();
		Map<String, Node> properties = new HashMap<String, Node>(size, 1);
		for (int i = 0; i < size; i++) {
			Node n = map.item(i);
			properties.put(n.getNodeName(), n);
		}
		return properties;
	}

	public static String formatNodeValue(
			final PropertiesFactory propertiesFactory, Node node, String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		if (!getBooleanValue(node, "replace", true)) {
			return value;
		}

		String replacePrefix = getNodeAttributeValue(node, "replace-prefix");
		String replaceSuffix = getNodeAttributeValue(node, "replace-suffix");
		replacePrefix = StringUtils.isEmpty(replacePrefix) ? "{"
				: replacePrefix;
		replaceSuffix = StringUtils.isEmpty(replaceSuffix) ? "}"
				: replaceSuffix;
		StringFormat stringFormat = new StringFormat(replacePrefix,
				replaceSuffix) {

			public String getValue(String key) {
				return propertiesFactory.getValue(key);
			}
		};
		return stringFormat.format(value);
	}

	public static String getNodeAttributeValue(
			PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (value == null || value.length() == 0) {
			return value;
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static String getNodeAttributeValueOrNodeContent(
			PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return formatNodeValue(propertiesFactory, node, value);
	}

	public static String getRequireNodeAttributeValue(
			PropertiesFactory propertiesFactory, Node node, String name) {
		String value = getNodeAttributeValue(propertiesFactory, node, name);
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return value;
	}

	public static <T> T newInstanceLoadAttributeBySetter(Class<T> type,
			final PropertiesFactory propertiesFactory, Node node,
			final PropertyMapper<String> mapper) {
		Map<String, Node> map = attributeAsMap(node);
		try {
			T t = ReflectUtils.newInstance(type);
			ReflectUtils.setProperties(type, t, map,
					new PropertyMapper<Node>() {
						public Object mapper(String name, Node value,
								Class<?> type) throws Exception {
							String v = formatNodeValue(propertiesFactory,
									value, value.getNodeValue());
							if (StringUtils.isEmpty(v)) {
								return null;
							}

							if (Class.class.isAssignableFrom(type)) {
								return Class.forName(v);
							}

							return mapper.mapper(name, v, type);
						}
					});
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

final class MyNodeList extends ArrayList<Node> implements NodeList {
	private static final long serialVersionUID = 1L;

	public Node item(int index) {
		return get(index);
	}

	public int getLength() {
		return size();
	}

}
