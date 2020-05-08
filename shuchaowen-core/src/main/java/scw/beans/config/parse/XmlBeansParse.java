package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.core.utils.ClassUtils;
import scw.mapper.Field;
import scw.util.ConfigUtils;
import scw.util.value.property.PropertyFactory;

/**
 * xml解析
 * 
 * @author shuchaowen
 *
 */
public final class XmlBeansParse extends AbstractValueFormat implements ConfigParse {
	
	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String filePath, String charset)
			throws Exception {
		String type = field.getSetter().getGenericType().toString();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		try {
			Class<?> toClz = ClassUtils.forName(type);
			return ConfigUtils.xmlToList(toClz, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}
}
