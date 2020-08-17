package scw.beans.xml;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.ioc.AbstractFieldIocProcessor;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.value.property.PropertyFactory;

public class XmlPropertiesIocProcessor extends AbstractFieldIocProcessor {
	private XmlBeanParameter xmlBeanParameter;

	public XmlPropertiesIocProcessor(Class<?> targetClass, XmlBeanParameter xmlBeanParameter) {
		super(MapperUtils.getMapper().getField(targetClass, xmlBeanParameter.getName(), null,
				FilterFeature.SUPPORT_SETTER));
		this.xmlBeanParameter = xmlBeanParameter;
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		getField().getSetter().set(bean,
				xmlBeanParameter.parseValue(getField().getSetter(), beanFactory, propertyFactory));
	}
}
