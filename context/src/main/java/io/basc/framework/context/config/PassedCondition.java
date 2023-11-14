package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.ConfigurableApplicationContext;

final class PassedCondition implements Condition {

	@Override
	public boolean matches(ConfigurableApplicationContext context, String beanName, BeanDefinition beanDefinition) {
		return true;
	}

}
