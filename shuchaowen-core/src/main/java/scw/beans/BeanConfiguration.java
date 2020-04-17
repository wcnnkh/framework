package scw.beans;

import java.util.Collection;

import scw.util.value.property.PropertyFactory;

public interface BeanConfiguration {
	Collection<BeanDefinition> getBeans(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception;
}
