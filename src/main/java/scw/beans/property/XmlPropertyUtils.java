package scw.beans.property;

import java.util.Properties;

import org.w3c.dom.Node;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Constants;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

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
		String file = XMLUtils.getNodeAttributeValue(node, "file");
		if (StringUtils.isEmpty(file)) {
			return null;
		}

		return PropertiesUtils.getProperties(file, getCharsetName(node));
	}

	public static String getCharsetName(Node node) {
		return XmlBeanUtils.getCharsetName(node, Constants.DEFAULT_CHARSET_NAME);
	}

	public static long getRefreshPeriod(Node node) {
		String value = XMLUtils.getNodeAttributeValue(node, "period");
		if (StringUtils.isEmpty(value)) {
			return 0;
		}

		long t = StringUtils.parseLong(value);
		if (t < 0) {
			return -1;
		}

		return XmlBeanUtils.getTimeUnit(node).toSeconds(t);
	}

	public static boolean isRefresh(Node node, boolean def) {
		return XMLUtils.getBooleanValueAndParent(node, "refresh", def);
	}

	public static boolean isSystem(Node node) {
		return XMLUtils.getBooleanValueAndParent(node, "system", false);
	}
}
