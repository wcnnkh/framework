package scw.beans.config.parse;

import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.io.ResourceUtils;
import scw.mapper.FieldContext;
import scw.mapper.FieldContextFilter;
import scw.mapper.MapperUtils;
import scw.util.value.StringValue;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public final class PropertiesParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext,
			String filePath, String charset) throws Exception {
		Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(filePath, charset,
				propertyFactory);
		if (ClassUtils.isPrimitiveOrWrapper(fieldContext.getField().getSetter().getType())
				|| TypeUtils.isString(fieldContext.getField().getSetter().getType())) {
			return ValueUtils.parse(properties.getProperty(fieldContext.getField().getSetter().getName()),
					fieldContext.getField().getSetter().getGenericType());
		} else if (Properties.class.isAssignableFrom(fieldContext.getField().getSetter().getType())) {
			return properties;
		} else {
			Class<?> fieldType = fieldContext.getField().getSetter().getType();
			try {
				Object obj = fieldType.newInstance();
				for (final Object key : properties.keySet()) {
					FieldContext field = MapperUtils.getMapper().getFieldContext(fieldType, null,
							new FieldContextFilter() {

								public boolean accept(FieldContext fieldContext) {
									if (fieldContext.getField().isSupportSetter()) {
										return fieldContext.getField().getSetter().getName().equals(key.toString());
									}
									return false;
								}
							});
					if (field == null) {
						continue;
					}

					String value = properties.getProperty(field.getField().getSetter().getName());
					field.getField().getSetter().set(obj,
							new StringValue(value).getAsObject(field.getField().getSetter().getGenericType()));
				}
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, fieldContext, name, getCharsetName());
	}
}