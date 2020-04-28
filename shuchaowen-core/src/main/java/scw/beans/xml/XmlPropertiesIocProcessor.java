package scw.beans.xml;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.beans.ioc.IocProcessor;
import scw.core.reflect.ReflectionUtils;
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
		Field field = ReflectionUtils.getField(targetClass,
				xmlBeanParameter.getDisplayName(), true);
		if (field == null) {
			return null;
		}

		return ReflectionUtils.setFieldValue(
				targetClass,
				field,
				bean,
				xmlBeanParameter.parseValue(beanFactory, propertyFactory,
						field.getGenericType()));
	}

	public boolean isGlobal() {
		return false;
	}
}
