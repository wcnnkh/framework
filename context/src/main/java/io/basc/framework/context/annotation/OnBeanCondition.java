package io.basc.framework.context.annotation;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.config.Condition;
import io.basc.framework.context.config.ConfigurableContext;

class OnBeanCondition implements Condition {

	@Override
	public boolean matches(ConfigurableContext context, String beanName, BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		return false;
	}

}
