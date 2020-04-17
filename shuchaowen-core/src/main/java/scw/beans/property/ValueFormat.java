package scw.beans.property;

import scw.beans.BeanFactory;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

public interface ValueFormat {
	Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition field, String name) throws Exception;
}
