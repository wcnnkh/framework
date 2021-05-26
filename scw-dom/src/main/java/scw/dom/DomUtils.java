package scw.dom;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.convert.Converter;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.io.ResourceLoader;
import scw.lang.NotFoundException;
import scw.lang.Nullable;
import scw.util.Accept;
import scw.util.Pair;
import scw.util.placeholder.PropertyResolver;
import scw.value.StringValue;

public final class DomUtils {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
	public static final NodeList EMPTY_NODE_LIST = new NodeList() {

		public Node item(int index) {
			return null;
		}

		public int getLength() {
			return 0;
		}
	};

	private static final DomBuilder DOM_BUILDER;

	static {
		DOCUMENT_BUILDER_FACTORY.setIgnoringElementContentWhitespace(Sys.env.getValue("dom.ignoring.element.content.whitespace", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY.setIgnoringComments(Sys.env.getValue("dom.ignoring.comments", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY.setCoalescing(Sys.env.getValue("dom.coalescing", boolean.class, true));
		DOCUMENT_BUILDER_FACTORY.setExpandEntityReferences(Sys.env.getValue("dom.expand.entity.references", boolean.class, false));
		DOCUMENT_BUILDER_FACTORY.setNamespaceAware(Sys.env.getValue("dom.namespace.aware", boolean.class, false));
		
		DomBuilder domBuilder = Sys.loadService(DomBuilder.class);
		DOM_BUILDER = domBuilder == null ? new DomBuilder(DOCUMENT_BUILDER_FACTORY, TRANSFORMER_FACTORY) : domBuilder;
	}

	private DomUtils() {
	}

	public static DomBuilder getDomBuilder() {
		return DOM_BUILDER;
	}

	public static Document getDocument(ResourceLoader resourceLoader, String path) throws NotFoundException {
		return getDomBuilder().parse(resourceLoader.getResource(path));
	}

	public static Element getRootElement(ResourceLoader resourceLoader, String xmlPath) {
		Document document = getDocument(resourceLoader, xmlPath);
		return document.getDocumentElement();
	}

	private static MyNodeList getIncludeNodeList(ResourceLoader resourceLoader, HashSet<String> includeHashSet,
			Node includeNode) {
		String file = getNodeAttributeValueOrNodeContent(includeNode, "file");
		if (StringUtils.isEmpty(file)) {
			return new MyNodeList();
		}

		if (includeHashSet.contains(file)) {
			throw new RuntimeException(file + "存在循环引用，请检查include地址");
		}

		includeHashSet.add(file);
		Document document = DomUtils.getDocument(resourceLoader, file);
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

	private static MyNodeList converIncludeNodeList(ResourceLoader resourceLoader, NodeList nodeList,
			HashSet<String> includeHashSet) {
		MyNodeList list = new MyNodeList();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (n.getNodeName().equalsIgnoreCase("include")) {
					MyNodeList n2 = getIncludeNodeList(resourceLoader, includeHashSet, n);
					list.addAll(converIncludeNodeList(resourceLoader, n2, includeHashSet));
				} else {
					list.add(n);
				}
			}
		}
		return list;
	}

	public static NodeList getChildNodes(Node node, @Nullable ResourceLoader resourceLoader) {
		if (node == null) {
			return null;
		}

		return resourceLoader != null
				? converIncludeNodeList(resourceLoader, node.getChildNodes(), new HashSet<String>())
				: node.getChildNodes();
	}

	public static List<Object> toRecursionList(Node node) {
		return toList(node, new Converter<Node, Object>() {

			public Object convert(Node o) {
				return toRecursionMap(o);
			}
		});
	}

	/**
	 * 将xml文档解析为map
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> toRecursionMap(Node node) {
		return toMap(node, new Converter<Node, Pair<String, Object>>() {

			public Pair<String, Object> convert(Node n) {
				NodeList nodeList = n.getChildNodes();
				Object v;
				if (nodeList == null || nodeList.getLength() == 0) {
					v = n.getTextContent();
				} else {
					List<Object> list = toList(n, new Converter<Node, Object>() {

						public Object convert(Node k) {
							return toRecursionMap(k);
						}
					});

					v = list == null ? n.getTextContent() : list;
				}
				return new Pair<String, Object>(n.getNodeName(), v);
			}
		});
	}

	public static List<Object> toList(Node node, Converter<Node, Object> nodeConvert) {
		if (ignoreNode(node)) {
			return null;
		}

		NodeList nodeList = node.getChildNodes();
		if (nodeList == null) {
			return null;
		}

		int len = nodeList.getLength();
		if (len == 0) {
			return null;
		}

		List<Object> list = new ArrayList<Object>(len);
		for (int i = 0; i < len; i++) {
			Node n = nodeList.item(i);
			if (ignoreNode(n)) {
				continue;
			}

			Object v = nodeConvert.convert(n);
			if (v == null) {
				continue;
			}

			list.add(v);
		}

		return list.isEmpty() ? null : list;
	}

	public static Map<String, Object> toMap(Node node, Converter<Node, Pair<String, Object>> nodeParse) {
		if (ignoreNode(node)) {
			return null;
		}

		NodeList nodeList = node.getChildNodes();
		if (nodeList == null) {
			return null;
		}

		int len = nodeList.getLength();
		if (len == 0) {
			return null;
		}

		Map<String, Object> map = new LinkedHashMap<String, Object>(len);
		for (int i = 0; i < len; i++) {
			Node n = nodeList.item(i);
			if (ignoreNode(node)) {
				continue;
			}

			Pair<String, Object> keyValuePair = nodeParse.convert(n);
			if (keyValuePair != null) {
				map.put(keyValuePair.getKey(), keyValuePair.getValue());
			}
		}

		return map.isEmpty() ? null : map;
	}

	public static String getNodeAttributeValue(Node node, String name) {
		return getNodeAttributeValue(node, name, null);
	}

	/**
	 * @param node
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getNodeAttributeValue(Type basicType, Node node, String name, T defaultValue) {
		String value = getNodeAttributeValue(node, name);
		if (value == null) {
			return defaultValue;
		} else {
			return (T) StringValue.parse(value, basicType);
		}
	}

	public static String getNodeAttributeValue(Node node, String name, String defaultValue) {
		if (node == null) {
			return null;
		}

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
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute [" + name + "], " + getDomBuilder().toString(node));
		}
		return value;
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (StringUtils.isEmpty(DomUtils.getNodeAttributeValue(node, n))) {
				throw new NotFoundException("not found attribute [" + n + "], " + getDomBuilder().toString(node));
			}
		}
	}

	public static Boolean getBooleanValueAndParent(Node node, String name, Boolean defaultValue) {
		Node parent = node.getParentNode();
		return getBooleanValue(node, name,
				parent == null ? defaultValue : getBooleanValueAndParent(parent, name, defaultValue));
	}

	public static Boolean getBooleanValue(Node node, String name, Boolean defaultValue) {
		String value = getNodeAttributeValue(node, name);
		return StringUtils.parseBoolean(value, defaultValue);
	}

	public static boolean ignoreNode(Node node) {
		return node == null || StringUtils.isEmpty(node.getNodeName()) || "#text".equals(node.getNodeName());
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
		Map<String, Node> properties = new LinkedHashMap<String, Node>(size, 1);
		for (int i = 0; i < size; i++) {
			Node n = map.item(i);
			properties.put(n.getNodeName(), n);
		}
		return properties;
	}

	public static String formatNodeValue(PropertyResolver propertyResolver, Node node, String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		if (!getBooleanValue(node, "replace", true)) {
			return value;
		}

		return propertyResolver.resolvePlaceholders(value);
	}

	public static String getNodeAttributeValue(PropertyResolver propertyResolver, Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (value == null || value.length() == 0) {
			return value;
		}

		return formatNodeValue(propertyResolver, node, value);
	}

	public static String getNodeAttributeValueOrNodeContent(PropertyResolver propertyResolver, Node node, String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return formatNodeValue(propertyResolver, node, value);
	}

	public static String getRequireNodeAttributeValueOrNodeContent(PropertyResolver propertyResolver, Node node,
			String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return formatNodeValue(propertyResolver, node, value);
	}

	public static String getRequireNodeAttributeValue(PropertyResolver propertyResolver, Node node, String name) {
		String value = getNodeAttributeValue(propertyResolver, node, name);
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return value;
	}

	public static NodeList toNodeList(final NamedNodeMap namedNodeMap) {
		if (namedNodeMap == null) {
			return null;
		}

		return new NodeList() {

			public Node item(int index) {
				return namedNodeMap.item(index);
			}

			public int getLength() {
				return namedNodeMap.getLength();
			}
		};
	}

	public static Node findNode(NodeList nodeList, Accept<Node> accept) {
		if (nodeList == null) {
			return null;
		}

		int len = nodeList.getLength();
		if (len == 0) {
			return null;
		}

		for (int i = 0; i < len; i++) {
			Node item = nodeList.item(i);
			if (item == null) {
				continue;
			}

			if (accept.accept(item)) {
				return item;
			}
		}
		return null;
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
