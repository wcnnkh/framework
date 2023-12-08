package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;

public class DefaultBeanFactory extends AbstractListableBeanFactory {

	public DefaultBeanFactory(Scope scope) {
		super(scope);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition beanDefinition = getBeanDefinitionOfCache(beanName);
		if (beanDefinition == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return beanDefinition;
	}
}
