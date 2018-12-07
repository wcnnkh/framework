package shuchaowen.beans.config.parse;

import java.io.File;

import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.config.ConfigParse;
import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.reflect.ClassUtils;
import shuchaowen.reflect.FieldInfo;

public class XmlToBeanMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		String type = field.getField().getGenericType().getTypeName();
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
