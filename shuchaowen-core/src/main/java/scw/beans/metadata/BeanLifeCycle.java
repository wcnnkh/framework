package scw.beans.metadata;

import scw.beans.BeanFactory;
import scw.beans.definition.BeanDefinition;
import scw.util.value.property.PropertyFactory;

public interface BeanLifeCycle {
	void initBefore(BeanFactory beanFactory, PropertyFactory propertyFactory,
			BeanDefinition definition, Object instance) throws Exception;

	void initAfter(BeanFactory beanFactory, PropertyFactory propertyFactory,
			BeanDefinition definition, Object instance) throws Exception; 

	void destroyBefore(BeanFactory beanFactory,
			PropertyFactory propertyFactory, BeanDefinition definition,
			Object instance) throws Exception;

	void destroyAfter(BeanFactory beanFactory, PropertyFactory propertyFactory,
			BeanDefinition definition, Object instance) throws Exception;
}
