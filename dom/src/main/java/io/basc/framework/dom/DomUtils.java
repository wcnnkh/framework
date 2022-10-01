package io.basc.framework.dom;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.env.Sys;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.value.StringValue;

public final class DomUtils {
	private static final DocumentTemplate TEMPLATE = Sys.getEnv()
			.getServiceLoader(DocumentTemplate.class, DocumentTemplate.class).first();

	public static DocumentTemplate getTemplate() {
		return TEMPLATE;
	}

	private DomUtils() {
	}

	public static List<Object> toRecursionList(Node node) {
		return toList(node, (o) -> toRecursionMap(o));
	}

	/**
	 * 将xml文档解析为map
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> toRecursionMap(Node node) {
		return toMap(node, (n) -> {
			NodeList nodeList = n.getChildNodes();
			Object v;
			if (nodeList == null || nodeList.getLength() == 0) {
				v = n.getTextContent();
			} else {
				List<Object> list = toList(n, (k) -> toRecursionMap(k));

				v = list == null ? n.getTextContent() : list;
			}
			return new Pair<String, Object>(n.getNodeName(), v);
		});
	}

	public static <E extends Throwable> List<Object> toList(Node node, Processor<Node, Object, E> nodeConvert)
			throws E {
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

			Object v = nodeConvert.process(n);
			if (v == null) {
				continue;
			}

			list.add(v);
		}

		return list.isEmpty() ? null : list;
	}

	public static <E extends Throwable> Map<String, Object> toMap(Node node,
			Processor<Node, Pair<String, Object>, E> nodeParse) throws E {
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

			Pair<String, Object> keyValuePair = nodeParse.process(n);
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
			throw new NotFoundException("not found attribute [" + name + "]");
		}
		return value;
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (StringUtils.isEmpty(DomUtils.getNodeAttributeValue(node, n))) {
				throw new NotFoundException("not found attribute [" + n + "]");
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

	public static String formatNodeValue(PlaceholderFormat placeholderFormat, Node node, String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		if (!getBooleanValue(node, "replace", true)) {
			return value;
		}

		return placeholderFormat.replacePlaceholders(value);
	}

	public static String getNodeAttributeValue(PlaceholderFormat placeholderFormat, Node node, String name) {
		String value = getNodeAttributeValue(node, name);
		if (value == null || value.length() == 0) {
			return value;
		}

		return formatNodeValue(placeholderFormat, node, value);
	}

	public static String getNodeAttributeValueOrNodeContent(PlaceholderFormat placeholderFormat, Node node,
			String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return formatNodeValue(placeholderFormat, node, value);
	}

	public static String getRequireNodeAttributeValueOrNodeContent(PlaceholderFormat placeholderFormat, Node node,
			String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return formatNodeValue(placeholderFormat, node, value);
	}

	public static String getRequireNodeAttributeValue(PlaceholderFormat placeholderFormat, Node node, String name) {
		String value = getNodeAttributeValue(placeholderFormat, node, name);
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

	public static Node findNode(NodeList nodeList, Predicate<Node> accept) {
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

			if (accept.test(item)) {
				return item;
			}
		}
		return null;
	}
}
