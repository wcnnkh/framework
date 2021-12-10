package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.mapper.Field;

public class ValueIocProcessor extends AbstractFieldIocProcessor {

	public ValueIocProcessor(Field field) {
		super(field);
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory)
			throws BeansException {
		Value value = getField().getSetter().getAnnotation(Value.class);
		if (value != null) {
			beanFactory.getInstance(value.processor()).process(beanDefinition, beanFactory, bean, getField(), value);
		}
	}
}
