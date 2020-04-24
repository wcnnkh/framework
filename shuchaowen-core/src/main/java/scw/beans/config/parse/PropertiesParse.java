package scw.beans.config.parse;

import java.lang.reflect.Field;
import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.io.ResourceUtils;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public final class PropertiesParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(filePath, charset, propertyFactory);
		if (ClassUtils.isPrimitiveOrWrapper(fieldDefinition.getField().getType())
				|| TypeUtils.isString(fieldDefinition.getField().getType())) {
			return ValueUtils.parse(properties.getProperty(fieldDefinition.getField().getName()), fieldDefinition.getField().getGenericType());
		} else if (Properties.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return properties;
		} else {
			Class<?> fieldType = fieldDefinition.getField().getType();
			try {
				Object obj = fieldType.newInstance();
				for (Object key : properties.keySet()) {
					Field field = ReflectionUtils.getField(fieldType, key.toString(), true);
					if (field == null) {
						continue;
					}

					String value = properties.getProperty(key.toString());
					ReflectionUtils.setFieldValueAutoType(fieldType, field, obj, value);
				}
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}