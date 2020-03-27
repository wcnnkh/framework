package scw.beans.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.util.value.property.PropertyFactory;

public class DefaultXmlBeanConfigFactory extends AbstractBeanConfiguration {

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
