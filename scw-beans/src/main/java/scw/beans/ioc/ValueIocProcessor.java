package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.annotation.Value;
import scw.mapper.Field;

public class ValueIocProcessor extends AbstractFieldIocProcessor {

	public ValueIocProcessor(Field field) {
		super(field);
	}

	@Override
	protected void processInternal(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		Value value = getField().getSetter().getAnnotation(Value.class);
		if (value != null) {
			beanFactory.getInstance(value.processer()).process(beanDefinition, beanFactory, bean,
					getField(), value);
		}
	}
}
