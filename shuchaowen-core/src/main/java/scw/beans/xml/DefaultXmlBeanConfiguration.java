package scw.beans.xml;

import org.w3c.dom.Node;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class DefaultXmlBeanConfiguration extends XmlBeanConfiguration {
	private static final String TAG_NAME = "bean";

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		if (getNodeList() != null) {
			for (int i = 0; i < getNodeList().getLength(); i++) {
				Node nRoot = getNodeList().item(i);
				if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
					BeanDefinition beanDefinition = new XmlBeanDefinition(beanFactory, propertyFactory, nRoot);
					beanDefinitions.add(beanDefinition);
				}
			}
		}
	}
}
