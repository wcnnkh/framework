package scw.beans.xml;

import org.w3c.dom.Node;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.net.http.HttpUtils;
import scw.resource.ResourceUtils;

public class XmlValue {
	private final String value;
	private final Node node;

	public XmlValue(Node node, String parentCharsetName) {
		this.node = node;
		String charset = XmlBeanUtils.getCharsetName(node, parentCharsetName);

		String value;
		String url = XMLUtils.getNodeAttributeValue(node, "url");

		if (!StringUtils.isNull(url)) {
			if (url.startsWith("file://")) {
				String path = url.substring(7);
				value = ResourceUtils.getResourceOperations().getFileContent(path, charset);
			} else if (url.startsWith("http://") || url.startsWith("https://")) {
				value = HttpUtils.getHttpClient().get(url);
			} else {
				String path = url.substring(7);
				value = ResourceUtils.getResourceOperations().getFileContent(path, charset);
			}
		} else {
			value = XMLUtils.getNodeAttributeValueOrNodeContent(node, "value");
		}
		this.value = value;
	}

	public XmlValue(String value, Node node) {
		this.node = node;
		this.value = value;
	}

	public boolean isRequire() {
		return XMLUtils.getBooleanValue(node, "require", false);
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

	public String formatValue(final PropertyFactory propertyFactory) {
		return XMLUtils.formatNodeValue(propertyFactory, node, value);
	}
}
