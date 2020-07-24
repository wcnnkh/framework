package scw.beans.ioc.value;

import java.lang.reflect.Modifier;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractValueProcesser implements ValueProcesser {
	final Logger logger = LoggerUtils.getLogger(AbstractValueProcesser.class);

	public void process(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value) {
		String name = StringUtils.isEmpty(value.value()) ? field.getSetter().getName() : value.value();
		String charsetName = StringUtils.isEmpty(value.charsetName()) ? Constants.DEFAULT_CHARSET_NAME
				: value.charsetName();
		processInteranl(beanDefinition, beanFactory, propertyFactory, bean, field, value, name, charsetName);
	}

	protected boolean isRegisterListener(BeanDefinition beanDefinition, Field field, Value value) {
		return (Modifier.isStatic(field.getSetter().getModifiers())
				|| (beanDefinition != null && beanDefinition.isSingleton())) && value.listener();
	}

	protected abstract void processInteranl(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Object bean, Field field, Value value, String name, String charsetName);
}
