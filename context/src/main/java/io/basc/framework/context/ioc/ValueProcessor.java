package io.basc.framework.context.ioc;

import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.mapper.Field;

public interface ValueProcessor {
	void process(Context context, Object bean, BeanDefinition definition, Field field, ValueDefinition value);
}