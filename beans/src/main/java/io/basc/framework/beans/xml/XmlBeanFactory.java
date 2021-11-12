package io.basc.framework.beans.xml;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.xml.XmlUtils;

import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlBeanFactory extends DefaultBeanFactory {
	private static final String XML_CONFIGURATION = "io.basc.framework.beans.xml";
	private static final String DEFAULT_CONFIG = "beans.xml";
	private static Logger logger = LoggerFactory.getLogger(XmlBeanFactory.class);
	private static final String TAG_NAME = "bean";
	private Resource configurationFile;

	public void readConfigurationFile(ConsumerProcessor<NodeList, Throwable> processor){
		XmlUtils.getTemplate().read(getConfigurationFile(), (document) -> {
			Node node = document.getDocumentElement();
			if (!"beans".equals(node.getNodeName())) {
				throw new BeansException("root tag name error [" + node.getNodeName() + "]");
			}
			
			NodeList nodeList = DomUtils.getTemplate().getChildNodes(node, getEnvironment());
			processor.process(nodeList);
		});
	}

	public Resource getConfigurationFile() {
		if(configurationFile == null) {
			String config = getEnvironment().getString(XML_CONFIGURATION);
			if(StringUtils.isNotEmpty(config)) {
				return getEnvironment().getResource(config);
			}
			
			return getEnvironment().getResource(DEFAULT_CONFIG);
		}
		return configurationFile;
	}

	public void setConfigurationFile(Resource configurationFile) {
		this.configurationFile = configurationFile;
	}

	@Override
	public synchronized void init() throws Throwable {
		readConfigurationFile((nodeList) -> {
			logger.info("Use config {}", getConfigurationFile().getDescription());
			loadXmlEnv(nodeList);
			addXmlBeanNameMapping(nodeList);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nRoot = nodeList.item(i);
				if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
					BeanDefinition beanDefinition = new XmlBeanDefinition(this, nRoot);
					registerDefinition(beanDefinition);
				}
			}
		});
		super.init();
	}

	private void loadXmlEnv(NodeList nodeList) {
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!"properties".equalsIgnoreCase(node.getNodeName())) {
				continue;
			}

			loadXmlEnv(null, null, node);
		}
	}

	private void loadXmlEnv(String prefix, String charsetName, Node node) {
		String prefixToUse = DomUtils.getNodeAttributeValue(node, "prefix");
		if (StringUtils.isEmpty(prefixToUse)) {
			prefixToUse = prefix;
		} else {
			prefixToUse = StringUtils.isEmpty(prefix) ? prefixToUse : (prefix + prefixToUse);
		}

		String charsetNameToUse = DomUtils.getNodeAttributeValue(node, "charsetName");
		if (StringUtils.isEmpty(charsetNameToUse)) {
			charsetNameToUse = charsetName;
		} else {
			charsetNameToUse = StringUtils.isEmpty(charsetName) ? charsetNameToUse : charsetName;
		}

		String file = DomUtils.getNodeAttributeValue(node, "file");
		if (!StringUtils.isEmpty(file)) {
			getEnvironment().loadProperties(prefixToUse, file, charsetNameToUse);
		}

		String name = DomUtils.getNodeAttributeValue(node, "name");
		if (StringUtils.isNotEmpty(name)) {
			name = StringUtils.isEmpty(prefixToUse) ? name : (prefixToUse + name);

			String url = getURL(node);
			if (StringUtils.isNotEmpty(url)) {
				String value = HttpUtils.getHttpClient().get(String.class, url).getBody();
				getEnvironment().put(name, value);
			}

			String value = DomUtils.getNodeAttributeValueOrNodeContent(getEnvironment(), node, "value");
			if (StringUtils.isNotEmpty(value)) {
				getEnvironment().put(name, value);
			}
		}

		NodeList nodeList = node.getChildNodes();
		if (nodeList != null) {
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node n = nodeList.item(i);
				if (n == null) {
					continue;
				}

				if (!"property".equalsIgnoreCase(n.getNodeName())) {
					continue;
				}

				loadXmlEnv(prefixToUse, charsetNameToUse, n);
			}
		}
	}

	private static String getURL(Node node) {
		return DomUtils.getNodeAttributeValue(node, "url");
	}

	private void addXmlBeanNameMapping(NodeList nodeList) {
		if (nodeList == null) {
			return;
		}

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if ("mapping".equalsIgnoreCase(node.getNodeName())) {
				Collection<String> names = Arrays.asList(
						StringUtils.splitToArray(DomUtils.getRequireNodeAttributeValue(getEnvironment(), node, "name")));
				String id = DomUtils.getRequireNodeAttributeValueOrNodeContent(getEnvironment(), node, "id");
				for (String name : names) {
					registerAlias(id, name);
				}
			}
		}
	}
}
