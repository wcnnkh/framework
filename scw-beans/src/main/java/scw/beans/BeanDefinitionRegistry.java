package scw.beans;

import scw.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory,
		AliasRegistry {
	
	Object getRegisterDefinitionMutex();
	
	BeanDefinition registerDefinition(String name, BeanDefinition beanDefinition);

}
