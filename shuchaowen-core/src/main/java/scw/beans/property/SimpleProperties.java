package scw.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.net.http.HttpUtils;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public class SimpleProperties extends AbstractProperties {

	public SimpleProperties(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
	}

	public Map<String, Property> getPropertyMap() {
		NodeList nodeList = getNode().getChildNodes();
		Map<String, Property> map = new HashMap<String, Property>();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (!"property".equalsIgnoreCase(n.getNodeName())) {
					continue;
				}

				Property property;
				if (!StringUtils.isEmpty(getFilePath(n))) {
					property = new FilePropertyValue(getPropertyFactory(), n);
				} else if (!StringUtils.isEmpty(getURL(n))) {
					property = new URLPropertyValue(getPropertyFactory(), n);
				} else {
					property = new ValuePropertyValue(getPropertyFactory(), n);
				}

				map.put(property.getName(), property);
			}
		}
		return map;
	}

	private static String getFilePath(Node node) {
		return XMLUtils.getNodeAttributeValue(node, "file");
	}

	private static String getURL(Node node) {
		return XMLUtils.getNodeAttributeValue(node, "url");
	}

	private static final class FilePropertyValue extends AbstractProperty {

		public FilePropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			String path = XMLUtils.formatNodeValue(getPropertyFactory(),
					getNode(), getFilePath(getNode()));
			return ResourceUtils.getResourceOperations().getContent(path,
					XmlPropertyUtils.getCharsetName(getNode()));
		}
	}

	private static final class URLPropertyValue extends AbstractProperty {

		public URLPropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			String url = XMLUtils.formatNodeValue(getPropertyFactory(),
					getNode(), getURL(getNode()));
			return HttpUtils.getHttpClient().get(url, String.class);
		}
	}

	private static final class ValuePropertyValue extends AbstractProperty {

		public ValuePropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			return XMLUtils.getNodeAttributeValueOrNodeContent(
					getPropertyFactory(), getNode(), "value");
		}
	}
}
