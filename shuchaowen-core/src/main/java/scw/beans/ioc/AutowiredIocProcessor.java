package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowired;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public class AutowiredIocProcessor extends DefaultFieldIocProcessor {

	public AutowiredIocProcessor(FieldDefinition fieldDefinition) {
		super(fieldDefinition);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Autowired s = getFieldDefinition().getAnnotatedElement().getAnnotation(
				Autowired.class);
		if (s != null) {
			String name = s.value();
			if (name.length() == 0) {
				name = getFieldDefinition().getField().getType().getName();
			}

			existDefaultValueWarnLog(bean);
			Object instance = beanFactory.getInstance(name);
			try {
				return getFieldDefinition().set(bean, instance);
			} catch (Exception e) {
				logger.error("clz="
						+ getFieldDefinition().getDeclaringClass().getName()
						+ ",fieldName="
						+ getFieldDefinition().getField().getName());
				throw e;
			}
		}
		return null;
	}
}
