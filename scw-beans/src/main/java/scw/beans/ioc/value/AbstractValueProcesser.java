package scw.beans.ioc.value;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mapper.Field;

public abstract class AbstractValueProcesser implements ValueProcesser {
	final Logger logger = LoggerUtils.getLogger(AbstractValueProcesser.class);

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
