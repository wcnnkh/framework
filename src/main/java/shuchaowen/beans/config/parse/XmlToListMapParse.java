package shuchaowen.beans.config.parse;

import java.io.File;

import shuchaowen.beans.BeanFactory;
import shuchaowen.beans.config.ConfigParse;
import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.reflect.FieldInfo;

public class XmlToListMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		return ConfigUtils.getDefaultXmlContent(file, "config");
	}
}
