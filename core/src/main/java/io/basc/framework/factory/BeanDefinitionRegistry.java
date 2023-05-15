package io.basc.framework.factory;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory, AliasRegistry {

	Object getDefinitionMutex();

	BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition);

	default BeanDefinition registerDefinition(BeanDefinition beanDefinition) {
		return registerDefinition(beanDefinition.getId(), beanDefinition);
	}
}
