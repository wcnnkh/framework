package scw.beans.config.parse;

import java.util.Collection;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.ResolvableType;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.util.ConfigUtils;
import scw.value.property.PropertyFactory;

public final class XmlToListMapParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String filePath,
			String charset) throws Exception {
		if (!Collection.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		if (!Map.class.isAssignableFrom(
				ResolvableType.forType(field.getSetter().getGenericType()).getGeneric(0).getRawClass())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		return ConfigUtils.getDefaultXmlContent(filePath, "config");
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}
