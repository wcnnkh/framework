package scw.beans.config.parse;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractCharsetNameValueFormat;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.ConfigUtils;

/**
 * xml解析
 * 
 * @author shuchaowen
 *
 */
public final class XmlBeansParse extends AbstractCharsetNameValueFormat implements ConfigParse {
	
	public XmlBeansParse(){
		this(Constants.DEFAULT_CHARSET_NAME);
	}
	
	public XmlBeansParse(String charsetName) {
		super(charsetName);
	}

	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		String type = fieldDefinition.getField().getGenericType().toString();
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
		String type = field.getGenericType().toString();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		Class<?> toClz = ClassUtils.forName(type);
		return ConfigUtils.xmlToList(toClz, name);
	}
}
