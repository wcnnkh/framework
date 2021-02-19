package scw.beans.xml;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.ioc.AbstractFieldIocProcessor;
import scw.mapper.FieldFeature;
import scw.mapper.MapperUtils;

public class XmlPropertiesIocProcessor extends AbstractFieldIocProcessor {
	private XmlBeanParameter xmlBeanParameter;

	public XmlPropertiesIocProcessor(Class<?> targetClass, XmlBeanParameter xmlBeanParameter) {
		super(MapperUtils.getMapper().getFields(targetClass).accept(FieldFeature.SUPPORT_SETTER)
				.find(xmlBeanParameter.getName(), null));
		this.xmlBeanParameter = xmlBeanParameter;
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		getField().getSetter().set(bean,
				xmlBeanParameter.parseValue(getField().getSetter(), beanFactory));
	}
}
