package scw.beans.proxy;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;

public interface ProxyBeanDefinitionFactory {
	BeanDefinition getBeanDefinition(Class<?> clazz, BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			String[] filterNames);
}
