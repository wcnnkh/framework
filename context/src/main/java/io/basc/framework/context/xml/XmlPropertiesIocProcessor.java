package io.basc.framework.context.xml;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.FieldIocProcessor;
import io.basc.framework.factory.BeansException;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.support.DefaultObjectMapping;

public class XmlPropertiesIocProcessor extends FieldIocProcessor {
	private XmlBeanParameter xmlBeanParameter;

	public XmlPropertiesIocProcessor(Context context, Class<?> targetClass, XmlBeanParameter xmlBeanParameter) {
		super(context, DefaultObjectMapping.getFields(targetClass).all().filter(FieldFeature.SUPPORT_SETTER)
				.getByName(xmlBeanParameter.getName(), null));
		this.xmlBeanParameter = xmlBeanParameter;
	}

	@Override
	public void processField(Object bean, BeanDefinition definition, Field field) throws BeansException {
		field.getSetter().set(bean, xmlBeanParameter.parseValue(getField().getSetter(), getContext()));
	}
}
