package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class ValueIocProcessor extends DefaultFieldIocProcessor{

	public ValueIocProcessor(FieldDefinition fieldDefinition) {
		super(fieldDefinition);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Value value = getFieldDefinition().getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			try {
				existDefaultValueWarnLog(bean);
				Object v = beanFactory.getInstance(value.format()).format(beanFactory, propertyFactory, getFieldDefinition(),
						value.value());
				if (v != null) {
					return getFieldDefinition().set(bean, v);
				}
			} catch (Exception e) {
				logger.error("clz=" + getFieldDefinition().getDeclaringClass().getName() + ",fieldName=" + getFieldDefinition().getField().getName());
				throw e;
			}
		}
		return null;
	}

}
