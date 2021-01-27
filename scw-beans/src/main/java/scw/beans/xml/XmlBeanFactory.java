package scw.beans.xml;

import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.support.DefaultBeanFactory;
import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.http.HttpUtils;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class XmlBeanFactory extends DefaultBeanFactory {
	public static final String DEFAULT_CONFIG = "beans.xml";
	public static final String CONFIG_NAME = "contextConfigLocation";
	private static Logger logger = LoggerUtils.getLogger(XmlBeanFactory.class);
	private static final String TAG_NAME = "bean";
	private NodeList nodeList;
	private String xml;
	
	public XmlBeanFactory(String xml) {
		this.xml = xml;
	}

	public NodeList getNodeList() {
		return nodeList == null ? DomUtils.EMPTY_NODE_LIST : nodeList;
	}
	
	public String getXml() {
		return xml;
	}

	@Override
	public void beforeInit() throws Throwable {
		Resource resource = null;
		if(StringUtils.isNotEmpty(xml)){
			resource = getEnvironment().getResource(xml);
		}
		
		if(resource == null || !resource.exists()){
			String config = getEnvironment().getString(CONFIG_NAME);
			if(StringUtils.isNotEmpty(config)){
				resource = getEnvironment().getResource(config);
			}
		}
		
		if(resource == null || !resource.exists()){
			resource = getEnvironment().getResource(DEFAULT_CONFIG);
		}
		
		if(resource != null && resource.exists()){
			this.nodeList = XmlBeanUtils.getRootNodeList(resource, getEnvironment());
			logger.info("Use config {}", resource);
			loadXmlEnv(nodeList);
			addXmlBeanNameMapping(nodeList);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nRoot = nodeList.item(i);
				if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
					BeanDefinition beanDefinition = new XmlBeanDefinition(this, nRoot);
					registerDefinition(beanDefinition.getId(), beanDefinition);
				}
			}
		}
		super.beforeInit();
	}
	
	private void loadXmlEnv(NodeList nodeList){
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
	
	private void loadXmlEnv(String prefix,
			String charsetName, Node node) {
		String prefixToUse = DomUtils.getNodeAttributeValue(node, "prefix");
		if (StringUtils.isEmpty(prefixToUse)) {
			prefixToUse = prefix;
		} else {
			prefixToUse = StringUtils.isEmpty(prefix) ? prefixToUse
					: (prefix + prefixToUse);
		}

		String charsetNameToUse = DomUtils.getNodeAttributeValue(node,
				"charsetName");
		if (StringUtils.isEmpty(charsetNameToUse)) {
			charsetNameToUse = charsetName;
		} else {
			charsetNameToUse = StringUtils.isEmpty(charsetName) ? charsetNameToUse
					: charsetName;
		}

		String file = DomUtils.getNodeAttributeValue(node, "file");
		if (!StringUtils.isEmpty(file)) {
			getEnvironment().loadProperties(prefixToUse, file, charsetNameToUse).register();
		}

		String name = DomUtils.getNodeAttributeValue(node, "name");
		if (StringUtils.isNotEmpty(name)) {
			name = StringUtils.isEmpty(prefixToUse) ? name
					: (prefixToUse + name);

			String url = getURL(node);
			if (StringUtils.isNotEmpty(url)) {
				String value = HttpUtils.getHttpClient().get(String.class, url)
						.getBody();
				getEnvironment().put(name, value);
			}

			String value = DomUtils.getNodeAttributeValueOrNodeContent(
					getEnvironment(), node, "value");
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
						StringUtils.commonSplit(DomUtils.getRequireNodeAttributeValue(getEnvironment(), node, "name")));
				String id = DomUtils.getRequireNodeAttributeValueOrNodeContent(getEnvironment(), node, "id");
				for(String name : names){
					registerAlias(id, name);
				}
			}
		}
	}
}
