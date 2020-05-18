package scw.beans.config.parse;

import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.io.ResourceUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.MapperUtils;
import scw.value.ValueUtils;
import scw.value.property.PropertyFactory;

public final class PropertiesParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field,
			String filePath, String charset) throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(filePath, charset,
				propertyFactory);
		if (ClassUtils.isPrimitiveOrWrapper(field.getSetter().getType())
				|| TypeUtils.isString(field.getSetter().getType())) {
			return ValueUtils.parse(properties.getProperty(field.getSetter().getName()),
					field.getSetter().getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getSetter().getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getSetter().getType();
			try {
				Object obj = fieldType.newInstance();
				for (final Object key : properties.keySet()) {
					Field keyField = MapperUtils.getMapper().getField(fieldType,
							new FieldFilter() {

								public boolean accept(Field field) {
									if (field.isSupportSetter()) {
										return field.getSetter().getName().equals(key.toString());
									}
									return false;
								}
							});
					if (keyField == null) {
						continue;
					}

					String value = properties.getProperty(keyField.getSetter().getName());
					MapperUtils.setStringValue(keyField, obj, value);
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
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}