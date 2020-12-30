package scw.beans;

import java.util.Collection;
import java.util.Map;

import scw.value.property.PropertyFactory;

public interface BeanConfiguration {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Collection<BeanDefinition> getBeans();
	
	Map<String, String> getNameMappingMap();
}
