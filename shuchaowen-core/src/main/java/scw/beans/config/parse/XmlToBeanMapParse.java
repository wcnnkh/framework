package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.reflect.FieldContext;
import scw.core.utils.ClassUtils;
import scw.util.ConfigUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlToBeanMapParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String filePath, String charset)
			throws Exception {
		String type = fieldContext.getField().getSetter().getGenericType().toString();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		String valueType = type.split(",")[1].trim();

		try {
			Class<?> toClz = ClassUtils.forName(valueType);
			return ConfigUtils.xmlToMap(toClz, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, fieldContext, name, getCharsetName());
	}
}
