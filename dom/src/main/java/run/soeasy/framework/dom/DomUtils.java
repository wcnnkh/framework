package run.soeasy.framework.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.function.Function;

public final class DomUtils {
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
			return KeyValue.of(n.getNodeName(), v);
		});
	}

	public static <E extends Throwable> List<Object> toList(Node node, Function<Node, Object, E> nodeConvert) throws E {
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

			Object v = nodeConvert.apply(n);
			if (v == null) {
				continue;
			}

			list.add(v);
		}

		return list.isEmpty() ? null : list;
	}

	public static <E extends Throwable> Map<String, Object> toMap(Node node,
			Function<Node, KeyValue<String, Object>, E> nodeParse) throws E {
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

			KeyValue<String, Object> keyValuePair = nodeParse.apply(n);
			if (keyValuePair != null) {
				map.put(keyValuePair.getKey(), keyValuePair.getValue());
			}
		}

		return map.isEmpty() ? null : map;
	}

	public static Source getNodeAttributeValue(Node node, String name) {
		if (node == null) {
			return Source.EMPTY;
		}

		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return Source.EMPTY;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? null : Source.of(n.getNodeValue());
	}

	public static String getNodeAttributeValueOrNodeContent(Node node, String name) {
		NamedNodeMap namedNodeMap = node.getAttributes();
		if (namedNodeMap == null) {
			return null;
		}

		Node n = namedNodeMap.getNamedItem(name);
		return n == null ? node.getTextContent() : n.getNodeValue();
	}

	public static Source getRequireNodeAttributeValue(Node node, String name) {
		Source value = getNodeAttributeValue(node, name);
		if (value == null) {
			throw new DomException("not found attribute [" + name + "]");
		}
		return value;
	}

	public static void requireAttribute(Node node, String... name) {
		for (String n : name) {
			if (DomUtils.getNodeAttributeValue(node, n) == null) {
				throw new DomException("not found attribute [" + n + "]");
			}
		}
	}

	public static Source getParentAttributeValue(Node node, String name) {
		Node parent = node.getParentNode();
		if (parent == null) {
			return Source.EMPTY;
		}

		Source value = getNodeAttributeValue(node, name);
		if (value == null) {
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
