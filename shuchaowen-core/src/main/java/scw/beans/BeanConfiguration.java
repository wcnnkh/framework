package scw.beans;

import java.util.Collection;

import scw.beans.definition.BeanDefinition;
import scw.util.value.property.PropertyFactory;

public interface BeanConfiguration {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Collection<BeanDefinition> getBeans();
}
