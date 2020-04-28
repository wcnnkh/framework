package scw.beans.property;

import scw.beans.AbstractBeanField;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class ValueBeanField extends AbstractBeanField {

	public ValueBeanField(FieldDefinition field) {
		super(field);
	}

	public void wired(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		Value value = fieldDefinition.getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			try {
				existDefaultValueWarnLog(bean);
				Object v = beanFactory.getInstance(value.format()).format(beanFactory, propertyFactory, fieldDefinition,
						value.value());
				if (v != null) {
					fieldDefinition.set(bean, v);
				}
			} catch (Exception e) {
				logger.error("clz=" + fieldDefinition.getDeclaringClass().getName() + ",fieldName=" + fieldDefinition.getField().getName());
				throw e;
			}
		}
	}
}
