package scw.beans;

import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public interface BeanField {
	void wired(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	FieldDefinition getFieldDefinition();
}
