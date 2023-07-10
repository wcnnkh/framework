package io.basc.framework.context.config.support;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.config.ConfigurableContext;

/**
 * 默认的注册策略
 * 
 * @author wcnnkh
 *
 */
public class DefaultContextBeanDefinitionRegistrationPolicy extends ContextBeanDefinitionRegistrationPolicys {
	@Override
	public boolean registerBeanDefinition(ConfigurableContext context, String beanName, BeanDefinition beanDefinition) {
		if (super.registerBeanDefinition(context, beanName, beanDefinition)) {
			return true;
		}

		// 默认的策略
		context.registerBeanDefinition(beanName, beanDefinition);
		return true;
	}
}
