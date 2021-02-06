package scw.beans;

import scw.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory,
		AliasRegistry {
	
	Object getDefinitionMutex();
	
	BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition);

	BeanDefinition registerDefinition(BeanDefinition beanDefinition);
}
