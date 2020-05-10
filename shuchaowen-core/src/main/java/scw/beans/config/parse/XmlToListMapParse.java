package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.mapper.Field;
import scw.util.ConfigUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlToListMapParse extends AbstractValueFormat
		implements ConfigParse {

	public Object parse(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Field field,
			String filePath, String charset) throws Exception {
		return ConfigUtils.getDefaultXmlContent(filePath, "config");
	}

	public Object format(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name,
				getCharsetName());
	}
}
