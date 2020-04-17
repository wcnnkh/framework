package scw.beans.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Node;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public class DefaultXmlBeanConfiguration extends XmlBeanConfiguration {
	private static final String TAG_NAME = "bean";

	public Collection<BeanDefinition> getBeans(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
		if (getNodeList() != null) {
			for (int i = 0; i < getNodeList().getLength(); i++) {
				Node nRoot = getNodeList().item(i);
				if (TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
					BeanDefinition beanDefinition = new XmlBeanDefinition(
							beanFactory, propertyFactory, nRoot);
					beanDefinitions.add(beanDefinition);
				}
			}
		}
		return beanDefinitions;
	}
}
