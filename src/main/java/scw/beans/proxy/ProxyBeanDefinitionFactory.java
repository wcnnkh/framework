package scw.beans.proxy;

import scw.beans.BeanDefinition;

public interface ProxyBeanDefinitionFactory {
	BeanDefinition getBeanDefinition(Class<?> clazz);
}
