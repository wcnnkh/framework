package shuchaowen.core.beans.config.parse;

import java.io.File;

import shuchaowen.common.FieldInfo;
import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;

public class XmlToListMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		return ConfigUtils.getDefaultXmlContent(file, "config");
	}
}
