package scw.beans.config.parse;

import java.io.File;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.common.FieldInfo;
import scw.common.utils.ClassUtils;
import scw.common.utils.ConfigUtils;

public final class XmlToBeanMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		String type = field.getField().getGenericType().toString();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		String valueType = type.split(",")[1].trim();
		
		try {
			Class<?> toClz = ClassUtils.forName(valueType);
			return ConfigUtils.xmlToMap(toClz, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
