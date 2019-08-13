package scw.beans.property;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Constants;
import scw.core.exception.AlreadyExistsException;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.core.utils.XTime;

public final class XmlPropertyUtils {
	private XmlPropertyUtils() {
	};

	public static String getPrefix(Node node) {
		return XMLUtils.getNodeAttributeValue(node, "prefix");
	}

	public static String getName(Node node) {
		return XMLUtils.getRequireNodeAttributeValue(node, "name");
	}

	/**
	 * 注意，此方法会返回空
	 * 
	 * @param node
	 * @return
	 */
	public static Properties getProperties(Node node) {
		String file = XMLUtils.getRequireNodeAttributeValue(node, "file");
		if (StringUtils.isEmpty(file)) {
			return null;
		}

		return PropertiesUtils.getProperties(file, getCharsetName(node));
	}

	public static String getCharsetName(Node node) {
		return XmlBeanUtils
				.getCharsetName(node, Constants.DEFAULT_CHARSET_NAME);
	}

	public static long getRefreshPeriod(Node node) {
		String value = XMLUtils.getNodeAttributeValue(node, "refresh");
		if (StringUtils.isEmpty(value)) {
			return 0;
		}

		String format = XMLUtils.getNodeAttributeValue(node, "refresh-format");
		return XTime.getTime(value,
				StringUtils.isEmpty(format) ? "yyyy-MM-dd HH:mm:ss,SSS"
						: format);
	}

	public static Map<String, Property> parse(Node rootNode) {
		Map<String, Property> map = new LinkedHashMap<String, Property>();
		String charset = XMLUtils.getNodeAttributeValue(rootNode, "charset");
		if (StringUtils.isNull(charset)) {
			charset = "UTF-8";
		}
		String prefix = XMLUtils.getNodeAttributeValue(rootNode, "prefix");

		String file = XMLUtils.getNodeAttributeValue(rootNode, "file");

		if (!StringUtils.isNull(file)) {
			Properties properties = PropertiesUtils
					.getProperties(file, charset);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String name = prefix == null ? entry.getKey().toString()
						: prefix + entry.getKey().toString();
				Property property = new Property(name, entry.getValue()
						.toString(), rootNode);
				map.put(property.getName(), property);
			}
		}

		NodeList nodeList = rootNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nRoot = nodeList.item(i);
			if (nRoot == null) {
				continue;
			}

			if (!"property".equalsIgnoreCase(nRoot.getNodeName())) {
				continue;
			}

			Property property = new Property(nRoot, charset);
			String name = prefix == null ? property.getName() : prefix
					+ property.getName();
			if (map.containsKey(name)) {
				throw new AlreadyExistsException(name);
			}
			map.put(name, property);
		}
		return map;
	}

	public static boolean isSystem(Node node, boolean def) {
		return StringUtils.parseBoolean(
				XMLUtils.getNodeAttributeValue(node, "system"), def);
	}

	public static boolean isSystem(Node node) {
		Node parent = node.getParentNode();
		return isSystem(node, parent == null ? false : isSystem(parent));
	}
}
