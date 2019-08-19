package scw.beans.property;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.StringParse;

public class PropertyFactoryFormat implements ValueFormat {

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name) {
		String v = propertyFactory.getProperty(name);
		if (v == null) {
			return null;
		}

		return StringParse.defaultParse(v, field.getType());
	}

}
