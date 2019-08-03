package scw.beans.config.parse;

import java.lang.reflect.Field;
import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringParse;
import scw.core.utils.SystemPropertyUtils;

public final class PropertiesParse implements ConfigParse {
	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		Properties properties = PropertiesUtils.getProperties(filePath, charset);
		if (ClassUtils.isPrimitiveOrWrapper(fieldDefinition.getField().getType())
				|| ClassUtils.isStringType(fieldDefinition.getField().getType())) {
			String v = SystemPropertyUtils.format(properties.getProperty(fieldDefinition.getField().getName()));
			return StringParse.DEFAULT.parse(v, fieldDefinition.getField().getType());
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
}