package shuchaowen.core.beans.config.parse;

import java.io.File;

import shuchaowen.common.FieldInfo;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;

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
