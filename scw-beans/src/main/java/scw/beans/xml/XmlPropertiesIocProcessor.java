package scw.beans.xml;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.ioc.AbstractFieldIocProcessor;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;

public class XmlPropertiesIocProcessor extends AbstractFieldIocProcessor {
	private XmlBeanParameter xmlBeanParameter;

	public XmlPropertiesIocProcessor(Class<?> targetClass, XmlBeanParameter xmlBeanParameter) {
		super(MapperUtils.getMapper().getFields(targetClass, FilterFeature.SUPPORT_SETTER)
				.find(xmlBeanParameter.getName(), null));
		this.xmlBeanParameter = xmlBeanParameter;
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws Exception {
		getField().getSetter().set(bean,
				xmlBeanParameter.parseValue(getField().getSetter(), beanFactory));
	}
}
