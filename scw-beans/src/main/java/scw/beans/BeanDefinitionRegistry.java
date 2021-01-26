package scw.beans;

import scw.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory,
		AliasRegistry {
	
	BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition);

}
