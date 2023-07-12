package io.basc.framework.context.config.support;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextBeanDefinitionRegistrationPolicy;

/**
 * 多个注册策略
 * 
 * @author wcnnkh
 *
 */
public class ContextBeanDefinitionRegistrationPolicys
		extends ConfigurableServices<ContextBeanDefinitionRegistrationPolicy>
		implements ContextBeanDefinitionRegistrationPolicy {

	public ContextBeanDefinitionRegistrationPolicys() {
		super(ContextBeanDefinitionRegistrationPolicy.class);
	}

	@Override
	public boolean registerBeanDefinition(ConfigurableContext context, String beanName, BeanDefinition beanDefinition) {
		for (ContextBeanDefinitionRegistrationPolicy policy : getServices()) {
			if (policy.registerBeanDefinition(context, beanName, beanDefinition)) {
				return true;
			}
		}
		return false;
	}

}
