package scw.beans.xml;

import org.w3c.dom.Node;

import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.http.HttpUtils;
import scw.io.ResourceUtils;
import scw.value.property.PropertyFactory;

public class XmlValue {
	private final String value;
	private final Node node;

	public XmlValue(Node node, String parentCharsetName) {
		this.node = node;
		String charset = XmlBeanUtils.getCharsetName(node, parentCharsetName);

		String value;
		String url = DomUtils.getNodeAttributeValue(node, "url");

		if (StringUtils.isNotEmpty(url)) {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				value = HttpUtils.getHttpClient().get(String.class, url).getBody();
			} else {
				value = ResourceUtils.getContent(ResourceUtils.getResourceOperations().getResource(url), charset);
			}
		} else {
			value = DomUtils.getNodeAttributeValueOrNodeContent(node, "value");
		}
		this.value = value;
	}

	public XmlValue(String value, Node node) {
		this.node = node;
		this.value = value;
	}

	public boolean isRequire() {
		return DomUtils.getBooleanValue(node, "require", false);
	}

	public String getValue() {
		return value;
	}

	public Node getNode() {
		return node;
	}

	public String getNodeAttributeValue(String name) {
		return DomUtils.getNodeAttributeValue(node, name);
	}

	public String formatValue(final PropertyFactory propertyFactory) {
		return DomUtils.formatNodeValue(propertyFactory, node, value);
	}
}
