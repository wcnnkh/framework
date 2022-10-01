package io.basc.framework.context.xml;

import org.w3c.dom.Node;

import io.basc.framework.dom.DomUtils;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderFormat;
import io.basc.framework.xml.XmlUtils;

public class XmlValue {
	private final String value;
	private final Node node;

	public XmlValue(ResourceLoader resourceLoader, Node node, String parentCharsetName) {
		this.node = node;
		String charset = XmlBeanUtils.getCharsetName(node, parentCharsetName);

		String value;
		String url = DomUtils.getNodeAttributeValue(node, "url");

		if (StringUtils.isNotEmpty(url)) {
			if (url.startsWith("http://") || url.startsWith("https://")) {
				value = HttpUtils.getHttpClient().get(String.class, url).getBody();
			} else {
				value = ResourceUtils.getContent(resourceLoader.getResource(url), charset);
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

	public String formatValue(final PlaceholderFormat format) {
		return DomUtils.formatNodeValue(format, node, value);
	}

	@Override
	public String toString() {
		return XmlUtils.getTemplate().getTransformer().toString(node);
	}
}
