package scw.beans.xml;

import scw.beans.BeanFactory;
import scw.beans.ioc.IocProcessor;
import scw.mapper.FieldContext;
import scw.mapper.FieldFilterType;
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
		FieldContext fieldContext = MapperUtils.getFieldFactory().getFieldContext(targetClass, xmlBeanParameter.getName(), FieldFilterType.SUPPORT_SETTER);
		if(fieldContext == null){
			return null;
		}

		fieldContext.getField().getSetter().set(bean, xmlBeanParameter.parseValue(beanFactory, propertyFactory,
						fieldContext.getField().getSetter().getGenericType()));
		return null;
	}

	public boolean isGlobal() {
		return false;
	}
}
