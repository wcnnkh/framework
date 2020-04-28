package scw.beans;

import scw.beans.annotation.Autowired;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class AutowiredBeanField extends AbstractBeanField {

	public AutowiredBeanField(FieldDefinition field) {
		super(field);
	}

	public void wired(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		Autowired s = fieldDefinition.getAnnotatedElement().getAnnotation(Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = fieldDefinition.getField().getType().getName();
			}

			existDefaultValueWarnLog(bean);
			Object instance = beanFactory.getInstance(name);
			try {
				fieldDefinition.set(bean, instance);
			} catch (Exception e) {
				logger.error("clz=" + fieldDefinition.getDeclaringClass().getName() + ",fieldName=" + fieldDefinition.getField().getName());
				throw e;
			}
		}
	}

}
