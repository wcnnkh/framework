package scw.beans.config.parse;

import java.lang.reflect.Field;
import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractCharsetNameValueFormat;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParse;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.TypeUtils;

public final class PropertiesParse extends AbstractCharsetNameValueFormat implements ConfigParse {

	public PropertiesParse() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public PropertiesParse(String charsetName) {
		super(charsetName);
	}

	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		Properties properties = ResourceUtils.getProperties(filePath, charset);
		if (ClassUtils.isPrimitiveOrWrapper(fieldDefinition.getField().getType())
				|| TypeUtils.isString(fieldDefinition.getField().getType())) {
			String v = SystemPropertyUtils.format(properties.getProperty(fieldDefinition.getField().getName()));
			return StringParse.defaultParse(v, fieldDefinition.getField().getGenericType());
		} else if (Properties.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return properties;
		} else {
			Class<?> fieldType = fieldDefinition.getField().getType();
			try {
				Object obj = fieldType.newInstance();
				for (Object key : properties.keySet()) {
					Field field = ReflectUtils.getField(fieldType, key.toString(), true);
					if (field == null) {
						continue;
					}

					String value = SystemPropertyUtils.format(properties.getProperty(key.toString()));
					ReflectUtils.setFieldValueAutoType(fieldType, field, obj, value);
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
		Properties properties = ResourceUtils.getProperties(name, getCharsetName());
		if (ClassUtils.isPrimitiveOrWrapper(field.getType()) || TypeUtils.isString(field.getType())) {
			String v = SystemPropertyUtils.format(properties.getProperty(field.getName()));
			return StringParse.defaultParse(v, field.getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getType();
			Object obj = fieldType.newInstance();
			for (Object key : properties.keySet()) {
				Field f = ReflectUtils.getField(fieldType, key.toString(), true);
				if (f == null) {
					continue;
				}

				String value = SystemPropertyUtils.format(properties.getProperty(key.toString()));
				ReflectUtils.setFieldValueAutoType(fieldType, f, obj, value);
			}
			return obj;
		}
	}
}