package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.BeanDefinition;

public interface Condition {
	public static final Condition PASSED = new PassedCondition();

	boolean matches(ConfigurableContext context, String beanName, BeanDefinition beanDefinition);
}
