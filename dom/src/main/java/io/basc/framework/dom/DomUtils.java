package io.basc.framework.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.env.Environment;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.util.Pair;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.Processor;

public final class DomUtils {
	private static final DocumentTemplate TEMPLATE = SPI.global()
			.getServiceLoader(DocumentTemplate.class, DocumentTemplate.class).getServices().first();

	public static DocumentTemplate getTemplate() {
		return TEMPLATE;
	}

	private DomUtils() {
	}

	public static List<Object> toRecursionList(Node node) {
		return toList(node, (o) -> toRecursionMap(o));
	}

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

	public static ValueWrapper getNodeAttributeValue(Node node, String name) {
		if (node == null) {
			return ValueWrapper.EMPTY;
		}

		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return ValueWrapper.EMPTY;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? ValueWrapper.EMPTY : ValueWrapper.of(n.getNodeValue());
	}

	public static String getNodeAttributeValueOrNodeContent(Node node, String name) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? node.getTextContent() : n.getNodeValue();
	}

	public static ValueWrapper getRequireNodeAttributeValue(Node node, String name) {
		ValueWrapper value = getNodeAttributeValue(node, name);
		if (!value.isPresent()) {
			throw new NotFoundException("not found attribute [" + name + "]");
		}
		return value;
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (!DomUtils.getNodeAttributeValue(node, n).isPresent()) {
				throw new NotFoundException("not found attribute [" + n + "]");
			}
		}
	}

	public static ValueWrapper getParentAttributeValue(Node node, String name) {
		Node parent = node.getParentNode();
		if (parent == null) {
			return ValueWrapper.EMPTY;
		}

		ValueWrapper value = getNodeAttributeValue(node, name);
		if (!value.isPresent()) {
			return getParentAttributeValue(parent, name);
		}
		return value;
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

	public static String formatNodeValue(Environment environment, Node node, String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		if (!getNodeAttributeValue(node, "replace").or(true).getAsBoolean()) {
			return value;
		}

		return environment.replacePlaceholders(value);
	}

	public static ValueWrapper getNodeAttributeValue(Environment environment, Node node, String name) {
		ValueWrapper value = getNodeAttributeValue(node, name);
		if (!value.isPresent()) {
			return value;
		}

		String str = value.getAsString();
		str = formatNodeValue(environment, node, str);
		return ValueWrapper.of(str);
	}

	public static String getNodeAttributeValueOrNodeContent(Environment environment, Node node, String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return formatNodeValue(environment, node, value);
	}

	public static String getRequireNodeAttributeValueOrNodeContent(Environment environment, Node node, String name) {
		String value = getNodeAttributeValueOrNodeContent(node, name);
		if (StringUtils.isEmpty(value)) {
			throw new NotFoundException("not found attribute " + name);
		}
		return formatNodeValue(environment, node, value);
	}

	public static ValueWrapper getRequireNodeAttributeValue(Environment environment, Node node, String name) {
		ValueWrapper value = getNodeAttributeValue(environment, node, name);
		if (!value.isPresent()) {
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

	public static Node findNode(NodeList nodeList, Predicate<? super Node> accept) {
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
