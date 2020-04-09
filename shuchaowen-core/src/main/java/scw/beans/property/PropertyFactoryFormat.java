package scw.beans.property;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.util.value.Value;
import scw.util.value.property.PropertyFactory;

@Configuration
public class PropertyFactoryFormat implements ValueFormat {

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name) {
		Value value = propertyFactory.get(name);
		if(value == null){
			return null;
		}
		
		return value.getAsObject(field.getGenericType());
	}

}
