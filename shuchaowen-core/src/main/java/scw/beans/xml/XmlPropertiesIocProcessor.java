package scw.beans.xml;

import scw.beans.BeanFactory;
import scw.beans.ioc.IocProcessor;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.value.property.PropertyFactory;

public class XmlPropertiesIocProcessor implements IocProcessor {
	private XmlBeanParameter xmlBeanParameter;
	private Class<?> targetClass;

	public XmlPropertiesIocProcessor(Class<?> targetClass,
			XmlBeanParameter xmlBeanParameter) {
		this.xmlBeanParameter = xmlBeanParameter;
		this.targetClass = targetClass;
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Field field = MapperUtils.getMapper().getField(targetClass, xmlBeanParameter.getName(), null, FilterFeature.SUPPORT_SETTER);
		if(field == null){
			return null;
		}

		field.getSetter().set(bean, xmlBeanParameter.parseValue(beanFactory, propertyFactory,
				field.getSetter().getGenericType()));
		return null;
	}

	public boolean isGlobal() {
		return false;
	}
}
