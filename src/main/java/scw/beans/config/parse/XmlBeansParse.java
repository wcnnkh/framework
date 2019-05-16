package scw.beans.config.parse;

import java.io.File;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ConfigUtils;

/**
 * xml解析
 * @author shuchaowen
 *
 */
public final class XmlBeansParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		String type = fieldDefinition.getField().getGenericType().toString();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		try {
			Class<?> toClz = Class.forName(type);
			return ConfigUtils.xmlToList(toClz, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
