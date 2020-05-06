package scw.beans.property;

import scw.beans.BeanFactory;
import scw.core.reflect.FieldContext;
import scw.util.value.property.PropertyFactory;

public interface ValueFormat {
	Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String name) throws Exception;
}
