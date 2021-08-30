package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

public abstract class AbstractValueProcesser implements ValueProcesser {
	final Logger logger = LoggerFactory.getLogger(AbstractValueProcesser.class);

	public void process(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value) {
		String name = StringUtils.isEmpty(value.value()) ? field.getSetter().getName() : value.value();
		String charsetName = StringUtils.isEmpty(value.charsetName()) ? beanFactory.getEnvironment().getCharsetName()
				: value.charsetName();
		try {
			processInteranl(beanDefinition, beanFactory, bean, field, value, name, Charset.forName(charsetName));
		} catch (Exception e) {
			throw new ValueException(field.toString(), e);
		}
	}

	protected boolean isRegisterListener(BeanDefinition beanDefinition, Field field, Value value) {
		return (Modifier.isStatic(field.getSetter().getModifiers())
				|| (beanDefinition != null && beanDefinition.isSingleton())) && value.listener();
	}

	protected abstract void processInteranl(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value, String name, Charset charset) throws Exception;
}
