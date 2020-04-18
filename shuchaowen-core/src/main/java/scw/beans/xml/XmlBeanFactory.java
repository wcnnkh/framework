package scw.beans.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanFactory;
import scw.beans.BeanConfiguration;
import scw.beans.BeanFactoryLifeCycle;
import scw.beans.property.XmlPropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.resource.ResourceUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
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

	@Override
	protected void addBeanConfiguration(BeanConfiguration beanConfiguration)
			throws Exception {
		if (beanConfiguration instanceof XmlBeanConfiguration) {
			((XmlBeanConfiguration) beanConfiguration)
					.setNodeList(getNodeList());
		}
		super.addBeanConfiguration(beanConfiguration);
	}

	@Override
	protected void addBeanFactoryLifeCycle(
			BeanFactoryLifeCycle beanFactoryLifeCycle) throws Exception {
		if (beanFactoryLifeCycle instanceof XmlBeanFactoryLifeCycle) {
			((XmlBeanFactoryLifeCycle) beanFactoryLifeCycle)
					.setNodeList(getNodeList());
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
			propertyFactory.add(xmlPropertyFactory);
		}
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
