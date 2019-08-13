package scw.beans.property;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.PropertyFactory;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.net.http.HttpUtils;

public class SimpleProperties extends AbstractProperties {

	public SimpleProperties(PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
	}

	public Map<String, PropertyValue> getPropertyMap() {
		NodeList nodeList = getNode().getChildNodes();
		Map<String, PropertyValue> map = new HashMap<String, PropertyValue>();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (!"property".equalsIgnoreCase(n.getNodeName())) {
					continue;
				}

				PropertyValue propertyValue;
				if (!StringUtils.isEmpty(getFilePath(n))) {
					propertyValue = new FilePropertyValue(getPropertyFactory(),
							getNode());
				} else if (!StringUtils.isEmpty(getURL(n))) {
					propertyValue = new URLPropertyValue(getPropertyFactory(),
							getNode());
				} else {
					propertyValue = new ValuePropertyValue(
							getPropertyFactory(), getNode());
				}

				map.put(propertyValue.getName(), propertyValue);
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

	private static final class FilePropertyValue extends AbstractPropertyValue {

		public FilePropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			String path = XMLUtils.formatNodeValue(getPropertyFactory(),
					getNode(), getFilePath(getNode()));
			return ConfigUtils.getFileContent(path,
					XmlPropertyUtils.getCharsetName(getNode()));
		}
	}

	private static final class URLPropertyValue extends AbstractPropertyValue {

		public URLPropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			String url = XMLUtils.formatNodeValue(getPropertyFactory(),
					getNode(), getURL(getNode()));
			return HttpUtils.doGet(url,
					XmlPropertyUtils.getCharsetName(getNode()));
		}

	}

	private static final class ValuePropertyValue extends AbstractPropertyValue {

		public ValuePropertyValue(PropertyFactory propertyFactory, Node node) {
			super(propertyFactory, node);
		}

		public String getValue() {
			return XMLUtils.getNodeAttributeValueOrNodeContent(
					getPropertyFactory(), getNode(), "value");
		}
	}
}
