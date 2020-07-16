package scw.beans.xml;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.ioc.IocProcessor;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.value.property.PropertyFactory;

public class XmlPropertiesIocProcessor implements IocProcessor {
	private XmlBeanParameter xmlBeanParameter;
	private Class<?> targetClass;

	public XmlPropertiesIocProcessor(Class<?> targetClass,
			XmlBeanParameter xmlBeanParameter) {
		this.xmlBeanParameter = xmlBeanParameter;
		this.targetClass = targetClass;
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Field field = MapperUtils.getMapper().getField(targetClass, xmlBeanParameter.getName(), null, FilterFeature.SUPPORT_SETTER);
		if(field == null){
			return ;
		}

		field.getSetter().set(bean, xmlBeanParameter.parseValue(beanFactory, propertyFactory,
				field.getSetter().getGenericType()));
	}

	public boolean isGlobal() {
		return false;
	}
}
