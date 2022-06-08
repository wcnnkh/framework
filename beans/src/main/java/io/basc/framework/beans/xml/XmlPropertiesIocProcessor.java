package io.basc.framework.beans.xml;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ioc.AbstractFieldIocProcessor;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;

public class XmlPropertiesIocProcessor extends AbstractFieldIocProcessor {
	private XmlBeanParameter xmlBeanParameter;

	public XmlPropertiesIocProcessor(Class<?> targetClass, XmlBeanParameter xmlBeanParameter) {
		super(Fields.getFields(targetClass).all().filter(FieldFeature.SUPPORT_SETTER)
				.getByName(xmlBeanParameter.getName(), null));
		this.xmlBeanParameter = xmlBeanParameter;
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory)
			throws BeansException {
		getField().getSetter().set(bean, xmlBeanParameter.parseValue(getField().getSetter(), beanFactory));
	}
}
