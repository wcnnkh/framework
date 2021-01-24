package scw.beans;

import scw.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory,
		AliasRegistry {

	BeanDefinition registerBeanDefinition(String name, BeanDefinition beanDefinition);

}
