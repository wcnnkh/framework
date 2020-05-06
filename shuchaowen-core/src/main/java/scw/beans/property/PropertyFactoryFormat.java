package scw.beans.property;

import scw.beans.BeanFactory;
import scw.core.instance.annotation.Configuration;
import scw.mapper.FieldContext;
import scw.util.value.Value;
import scw.util.value.property.PropertyFactory;

@Configuration
public class PropertyFactoryFormat implements ValueFormat {

	public Object format(BeanFactory beanFactory,
			PropertyFactory propertyFactory, FieldContext fieldContext, String name) {
		Value value = propertyFactory.get(name);
		if (value == null) {
			return null;
		}

		return value.getAsObject(fieldContext.getField().getSetter().getGenericType());
	}

}
