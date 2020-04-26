package scw.beans;

import java.util.Collection;
import java.util.Map;

import scw.util.value.property.PropertyFactory;

public interface BeanConfiguration {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Collection<BeanDefinition> getBeans();
	
	Map<String, String> getNameMappingMap();
}
