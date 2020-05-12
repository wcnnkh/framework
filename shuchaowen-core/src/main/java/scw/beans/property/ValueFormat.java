package scw.beans.property;

import scw.beans.BeanFactory;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public interface ValueFormat {
	Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name) throws Exception;
}
