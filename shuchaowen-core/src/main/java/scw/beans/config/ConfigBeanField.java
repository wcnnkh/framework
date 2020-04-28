package scw.beans.config;

import scw.beans.AbstractBeanField;
import scw.beans.BeanFactory;
import scw.beans.annotation.Config;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class ConfigBeanField extends AbstractBeanField {

	public ConfigBeanField(FieldDefinition fieldDefinition) {
		super(fieldDefinition);
	}

	public void wired(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		Config config = fieldDefinition.getAnnotatedElement().getAnnotation(Config.class);
		if (config != null) {
			Object value = null;
			try {
				existDefaultValueWarnLog(bean);
				value = beanFactory.getInstance(config.parse()).parse(beanFactory, propertyFactory, fieldDefinition,
						config.value(), config.charset());
				fieldDefinition.set(bean, value);
			} catch (Exception e) {
				logger.error("clz=" + fieldDefinition.getDeclaringClass().getName() + ",fieldName=" + fieldDefinition.getField().getName());
				throw e;
			}
		}
	}
}
