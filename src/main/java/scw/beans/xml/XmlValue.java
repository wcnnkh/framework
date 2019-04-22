package scw.beans.xml;

import org.w3c.dom.Node;

import scw.beans.property.PropertiesFactory;
import scw.common.StringFormat;
import scw.common.utils.ConfigUtils;
import scw.common.utils.StringUtils;
import scw.common.utils.XMLUtils;
import scw.net.http.HttpUtils;

public class XmlValue {
	private final String value;
	private final boolean replace;
	private final String replace_prefix;
	private final String replace_suffix;
	private final Node node;

	public XmlValue(Node node, String parentCharsetName) {
		this.node = node;
		this.replace = XMLUtils.getBooleanValue(node, "replace", true);
		this.replace_prefix = XMLUtils.getNodeAttributeValue(node, "replace-prefix");
		this.replace_suffix = XMLUtils.getNodeAttributeValue(node, "replace-suffix");
		String charset = XmlBeanUtils.getCharsetName(node, parentCharsetName);

		String value;
		String url = XMLUtils.getNodeAttributeValue(node, "url");
		if (!StringUtils.isNull(url)) {
			if (url.startsWith("file://")) {
				String path = url.substring(7);
				value = ConfigUtils.getFileContent(path, charset);
			} else if (url.startsWith("http://") || url.startsWith("https://")) {
				value = HttpUtils.doGet(url);
			} else {
				String path = url.substring(7);
				value = ConfigUtils.getFileContent(path, charset);
			}
		} else {
			value = XMLUtils.getNodeAttributeValueOrNodeContent(node, "value");
		}
		this.value = value;
	}

	public XmlValue(String value, Node node) {
		this.node = node;
		this.value = value;
		this.replace = XMLUtils.getBooleanValue(node, "replace", true);
		this.replace_prefix = XMLUtils.getNodeAttributeValue(node, "replace-prefix");
		this.replace_suffix = XMLUtils.getNodeAttributeValue(node, "replace-suffix");
	}

	public String getValue() {
		return value;
	}

	public Node getNode() {
		return node;
	}

	public String getNodeAttributeValue(String name) {
		return XMLUtils.getNodeAttributeValue(node, name);
	}

	public String getNodeAttributeValue(PropertiesFactory propertiesFactory, String name) {
		return XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, name);
	}

	public String formatValue(final PropertiesFactory propertiesFactory) {
		if (StringUtils.isNull(value)) {
			return value;
		}

		if (!replace) {
			return value;
		}

		String replacePrefix = StringUtils.isNull(replace_prefix) ? "{" : replace_prefix;
		String replaceSuffix = StringUtils.isNull(replace_suffix) ? "}" : replace_suffix;
		StringFormat stringFormat = new StringFormat(replacePrefix, replaceSuffix) {

			@Override
			protected String getValue(String key) {
				return propertiesFactory.getValue(key);
			}
		};
		return stringFormat.format(value);
	}
}
