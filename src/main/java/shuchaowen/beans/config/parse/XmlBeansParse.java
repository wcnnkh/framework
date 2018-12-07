package shuchaowen.beans.config.parse;

import java.io.File;

import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.config.ConfigParse;
import shuchaowen.common.FieldInfo;
import shuchaowen.common.utils.ConfigUtils;

/**
 * xml解析
 * @author shuchaowen
 *
 */
public class XmlBeansParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		String type = field.getField().getGenericType().getTypeName();
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
