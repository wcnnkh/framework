package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractValueProcesser implements ValueProcesser {

	public void process(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value) {
		String name = StringUtils.isEmpty(value.value()) ? field.getSetter().getName() : value.value();
		String charsetName = StringUtils.isEmpty(value.charsetName()) ? Constants.DEFAULT_CHARSET_NAME
				: value.charsetName();
		processInteranl(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName);
	}

	protected boolean isRegisterListener(BeanDefinition beanDefinition, Field field) {
		return beanDefinition == null || beanDefinition.isSingleton();
	}

	protected abstract void processInteranl(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Object bean, Field field, Value value, String name, String charsetName);
}
