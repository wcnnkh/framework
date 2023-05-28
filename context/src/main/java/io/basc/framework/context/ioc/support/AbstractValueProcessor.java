package io.basc.framework.context.ioc.support;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.context.ioc.ValueException;
import io.basc.framework.context.ioc.ValueProcessor;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.util.StringUtils;

public abstract class AbstractValueProcessor implements ValueProcessor {
	final Logger logger = LoggerFactory.getLogger(AbstractValueProcessor.class);

	public void process(Context context, Object bean, BeanDefinition definition, Field field,
			ValueDefinition valueDefinition) {
		String charsetName = StringUtils.isEmpty(valueDefinition.getCharsetName()) ? context.getCharsetName()
				: valueDefinition.getCharsetName();
		try {
			processInteranl(definition, context, bean, field, valueDefinition, Charset.forName(charsetName));
		} catch (Exception e) {
			throw new ValueException(field.toString(), e);
		}
	}

	protected boolean isRegisterListener(BeanDefinition beanDefinition, Field field, ValueDefinition valueDefinition) {
		return (Modifier.isStatic(field.getSetter().getModifiers())
				|| (beanDefinition != null && beanDefinition.isSingleton())) && valueDefinition.isListener();
	}

	protected abstract void processInteranl(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition valueDefinition, Charset charset) throws Exception;
}
