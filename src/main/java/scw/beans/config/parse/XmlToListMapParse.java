package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ConfigUtils;

public final class XmlToListMapParse implements ConfigParse {
	public Object parse(BeanFactory beanFactory,
			FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		return ConfigUtils.getDefaultXmlContent(filePath, "config");
	}
}
