package scw.beans.configuration;

import java.util.Collection;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public interface BeanConfiguration {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Collection<BeanDefinition> getBeans();
}
