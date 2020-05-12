package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class ValueIocProcessor extends DefaultFieldIocProcessor {

	public ValueIocProcessor(Field field) {
		super(field);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Value value = getField().getSetter()
				.getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			existDefaultValueWarnLog(bean);
			Object v = beanFactory.getInstance(value.format()).format(
					beanFactory, propertyFactory, getField(),
					value.value());
			if (v != null) {
				getField().getSetter().set(bean, v);
			}
		}
		return null;
	}

}
