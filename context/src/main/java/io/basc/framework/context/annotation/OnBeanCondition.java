package io.basc.framework.context.annotation;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.ConfigurableApplicationContext;
import io.basc.framework.context.config.Condition;

class OnBeanCondition implements Condition {

	@Override
	public boolean matches(ConfigurableApplicationContext context, String beanName, BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		return false;
	}

}
