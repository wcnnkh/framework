package scw.beans.config.parse;

import java.io.File;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.common.FieldInfo;
import scw.common.utils.ConfigUtils;

public class XmlToListMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		return ConfigUtils.getDefaultXmlContent(file, "config");
	}
}
