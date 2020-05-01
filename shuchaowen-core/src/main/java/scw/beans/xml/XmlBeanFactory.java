package scw.beans.xml;

import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanConfiguration;
import scw.beans.BeanFactoryLifeCycle;
import scw.beans.DefaultBeanFactory;
import scw.beans.property.XmlPropertyFactory;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.xml.XMLUtils;

public class XmlBeanFactory extends DefaultBeanFactory {
	private NodeList nodeList;
	private XmlPropertyFactory xmlPropertyFactory;
	private String xmlConfigPath;

	public XmlBeanFactory(String xmlConfigPath) {
		super();
		this.xmlConfigPath = xmlConfigPath;
	}

	protected NodeList getNodeList() {
		return nodeList;
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
						StringUtils.commonSplit(XMLUtils.getRequireNodeAttributeValue(propertyFactory, node, "name")));
				String id = XMLUtils.getRequireNodeAttributeValueOrNodeContent(propertyFactory, node, "id");
				addBeanNameMapping(names, id, false);
			}
		}
	}

	@Override
	protected void addBeanConfiguration(BeanConfiguration beanConfiguration) throws Exception {
		if (beanConfiguration instanceof XmlBeanConfiguration) {
			((XmlBeanConfiguration) beanConfiguration).setNodeList(getNodeList());
		}
		super.addBeanConfiguration(beanConfiguration);
	}

	@Override
	protected void addBeanFactoryLifeCycle(BeanFactoryLifeCycle beanFactoryLifeCycle) throws Exception {
		if (beanFactoryLifeCycle instanceof XmlBeanFactoryLifeCycle) {
			if (getNodeList() == null) {
				return;
			}

			((XmlBeanFactoryLifeCycle) beanFactoryLifeCycle).setNodeList(getNodeList());
		}
		super.addBeanFactoryLifeCycle(beanFactoryLifeCycle);
	}

	public void init() throws Exception {
		if (StringUtils.isNotEmpty(xmlConfigPath)) {
			if (ResourceUtils.getResourceOperations().isExist(xmlConfigPath)) {
				Node root = XmlBeanUtils.getRootNode(xmlConfigPath);
				this.nodeList = XMLUtils.getChildNodes(root, true);
			} else {
				logger.info("not use:{}", xmlConfigPath);
			}

			this.xmlPropertyFactory = new XmlPropertyFactory(nodeList);
			propertyFactory.addBasePropertyFactory(xmlPropertyFactory);
		}
		addXmlBeanNameMapping(nodeList);
		addBeanConfiguration(new DefaultXmlBeanConfiguration());
		super.init();
	}

	public void destroy() throws Exception {
		if (xmlPropertyFactory != null) {
			xmlPropertyFactory.destroy();
		}
		super.destroy();
	}
}
