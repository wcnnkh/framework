package scw.beans.config.parse;

import java.lang.reflect.Field;
import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractCharsetNameValueFormat;
import scw.core.Constants;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.io.resource.ResourceUtils;
import scw.util.value.StringValue;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public final class PropertiesParse extends AbstractCharsetNameValueFormat implements ConfigParse {

	public PropertiesParse() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public PropertiesParse(String charsetName) {
		super(charsetName);
	}

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getProperties(filePath, charset, propertyFactory);
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

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getProperties(name, getCharsetName(), propertyFactory);
		if (ClassUtils.isPrimitiveOrWrapper(field.getType()) || TypeUtils.isString(field.getType())) {
			String v = properties.getProperty(field.getName());
			if(v == null){
				return null;
			}
			
			return new StringValue(v).getAsObject(field.getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getType();
			Object obj = fieldType.newInstance();
			for (Object key : properties.keySet()) {
				Field f = ReflectionUtils.getField(fieldType, key.toString(), true);
				if (f == null) {
					continue;
				}
				ReflectionUtils.setFieldValueAutoType(fieldType, f, obj, properties.getProperty(key.toString()));
			}
			return obj;
		}
	}
}