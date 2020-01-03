package scw.beans.xml;

import org.w3c.dom.NodeList;

import scw.beans.BeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;

public interface XmlBeanConfigFactory extends BeanConfigFactory {
	void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			NodeList nodeList) throws Exception;
}
