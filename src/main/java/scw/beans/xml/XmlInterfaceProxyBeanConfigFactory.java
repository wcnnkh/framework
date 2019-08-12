package scw.beans.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public class XmlInterfaceProxyBeanConfigFactory extends
		AbstractBeanConfigFactory {
	public XmlInterfaceProxyBeanConfigFactory(BeanFactory beanFactory,
			PropertyFactory propertyFactory, NodeList rootNodeList,
			String[] filterNames) throws Exception {
		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node nRoot = rootNodeList.item(i);
			if ("interface".equalsIgnoreCase(nRoot.getNodeName())) {
				BeanDefinition beanDefinition = new XmlInterfaceProxyBeanDefinition(
						beanFactory, propertyFactory, nRoot, filterNames);
				addBean(beanDefinition);
			}
		}
	}
}
