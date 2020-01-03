package scw.beans.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;

public class DefaultXmlBeanConfigFactory extends AbstractBeanConfigFactory {

	public DefaultXmlBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, NodeList rootNodeList, String beanTagName)
			throws Exception {
		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node nRoot = rootNodeList.item(i);
			if (beanTagName.equalsIgnoreCase(nRoot.getNodeName())) {
				BeanDefinition beanDefinition = new XmlBeanDefinition(valueWiredManager, beanFactory, propertyFactory,
						nRoot);
				addBean(beanDefinition);
			}
		}
	}
}
