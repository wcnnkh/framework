package io.basc.framework.context.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.event.Observable;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

public class XmlContextPostProcessor implements ContextPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(XmlContextPostProcessor.class);
	private static final String TAG_NAME = "bean";

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		for (Resource resource : context.getResources()) {
			if (resource.exists() && resource.getName().endsWith(".xml")) {
				logger.info("Use config {}", resource.getDescription());

				XmlBeanUtils.read(context.getResourceLoader(), resource, (nodeList) -> {
					loadXmlEnv(context, nodeList);
					addXmlBeanNameMapping(context, nodeList);

					for (int i = 0; i < nodeList.getLength(); i++) {
						Node nRoot = nodeList.item(i);
						if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
							BeanDefinition beanDefinition = new XmlBeanDefinition(context, nRoot);
							context.registerDefinition(beanDefinition);
						}
					}
				});
			}
		}
	}

	private void loadXmlEnv(ConfigurableContext context, NodeList nodeList) {
		for (int i = 0, size = nodeList.getLength(); i < size; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!"properties".equalsIgnoreCase(node.getNodeName())) {
				continue;
			}

			loadXmlEnv(context, null, null, node);
		}
	}

	private void loadXmlEnv(ConfigurableEnvironment environment, String prefix, String charsetName, Node node) {
		String prefixToUse = DomUtils.getNodeAttributeValue(node, "prefix").getAsString();
		if (StringUtils.isEmpty(prefixToUse)) {
			prefixToUse = prefix;
		} else {
			prefixToUse = StringUtils.isEmpty(prefix) ? prefixToUse : (prefix + prefixToUse);
		}

		String charsetNameToUse = DomUtils.getNodeAttributeValue(node, "charsetName").getAsString();
		if (StringUtils.isEmpty(charsetNameToUse)) {
			charsetNameToUse = charsetName;
		} else {
			charsetNameToUse = StringUtils.isEmpty(charsetName) ? charsetNameToUse : charsetName;
		}

		String file = DomUtils.getNodeAttributeValue(node, "file").getAsString();
		if (!StringUtils.isEmpty(file)) {
			final String propertyPrefix = prefixToUse;
			Observable<Properties> observableProperties = environment.getProperties(file);
			if (StringUtils.isNotEmpty(prefixToUse)) {
				observableProperties = observableProperties.map((e) -> {
					Properties properties = new Properties();
					for (Entry<Object, Object> entry : e.entrySet()) {
						properties.put(propertyPrefix + entry.getKey(), entry.getValue());
					}
					return properties;
				});
			}
			environment.source(observableProperties);
		}

		String name = DomUtils.getNodeAttributeValue(node, "name").getAsString();
		if (StringUtils.isNotEmpty(name)) {
			name = StringUtils.isEmpty(prefixToUse) ? name : (prefixToUse + name);

			String url = getURL(node);
			if (StringUtils.isNotEmpty(url)) {
				String value = HttpUtils.getClient().get(String.class, url).getBody();
				environment.getProperties().put(name, value);
			}

			String value = DomUtils.getNodeAttributeValueOrNodeContent(environment, node, "value");
			if (StringUtils.isNotEmpty(value)) {
				environment.getProperties().put(name, value);
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

				loadXmlEnv(environment, prefixToUse, charsetNameToUse, n);
			}
		}
	}

	private static String getURL(Node node) {
		return DomUtils.getNodeAttributeValue(node, "url").getAsString();
	}

	private void addXmlBeanNameMapping(ConfigurableContext context, NodeList nodeList) {
		if (nodeList == null) {
			return;
		}

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if ("mapping".equalsIgnoreCase(node.getNodeName())) {
				Collection<String> names = Arrays.asList(StringUtils
						.splitToArray(DomUtils.getRequireNodeAttributeValue(context, node, "name").getAsString()));
				String id = DomUtils.getRequireNodeAttributeValueOrNodeContent(context, node, "id");
				for (String name : names) {
					context.registerAlias(id, name);
				}
			}
		}
	}
}
