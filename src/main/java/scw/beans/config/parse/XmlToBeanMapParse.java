package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ClassUtils;
import scw.core.utils.ConfigUtils;

public final class XmlToBeanMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception{
		String type = fieldDefinition.getField().getGenericType().toString();
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
}
