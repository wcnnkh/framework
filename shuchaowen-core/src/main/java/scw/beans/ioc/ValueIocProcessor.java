package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class ValueIocProcessor extends DefaultFieldIocProcessor {

	public ValueIocProcessor(Field field) {
		super(field);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		Value value = getField().getSetter().getAnnotatedElement().getAnnotation(Value.class);
		if (value != null) {
			checkField(bean);
			beanFactory.getInstance(value.processer()).process(beanDefinition, beanFactory, propertyFactory, bean,
					getField(), value);
		}
	}
}
