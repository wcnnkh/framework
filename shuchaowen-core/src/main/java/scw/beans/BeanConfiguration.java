package scw.beans;

import java.util.Collection;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.value.property.PropertyFactory;

@Bean(proxy=false)
public interface BeanConfiguration {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Collection<BeanDefinition> getBeans();
	
	Map<String, String> getNameMappingMap();
}
