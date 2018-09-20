package shuchaowen.core.beans.config.parse;

import java.io.File;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;

public class XmlToListMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		return ConfigUtils.getDefaultXmlContent(file, "config");
	}
}
