package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.mapper.FieldContext;
import scw.util.value.property.PropertyFactory;

public class ValueIocProcessor extends DefaultFieldIocProcessor {

	public ValueIocProcessor(FieldContext fieldContext) {
		super(fieldContext);
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Value value = getFieldContext().getField().getSetter()
				.getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			existDefaultValueWarnLog(bean);
			Object v = beanFactory.getInstance(value.format()).format(
					beanFactory, propertyFactory, getFieldContext(),
					value.value());
			if (v != null) {
				getFieldContext().getField().getSetter().set(bean, v);
			}
		}
		return null;
	}

}
