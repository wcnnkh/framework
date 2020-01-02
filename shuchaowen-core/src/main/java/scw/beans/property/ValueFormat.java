package scw.beans.property;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public interface ValueFormat {
	Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name) throws Exception;
}
