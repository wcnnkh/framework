package scw.mq.support;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;
import scw.xml.XmlAttributeMapping;

public class XmlAmqpConfigMapping extends XmlAttributeMapping{
	private BeanFactory beanFactory;
	
	public XmlAmqpConfigMapping(BeanFactory beanFactory, PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
		this.beanFactory = beanFactory;
	}

	@Override
	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		if (name.equals("exchange")) {
			return beanFactory.getInstance(value);
		}
		return super.getNodeValue(name, value, type, field, node);
	}
}
