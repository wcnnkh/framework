package shuchaowen.core.beans.config.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;

/**
 * xml解析
 * @author shuchaowen
 *
 */
public class XmlBeansParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(file, "config");
		List<Object> objList = new ArrayList<Object>();
		String type = field.getField().getGenericType().getTypeName();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		try {
			Class<?> toClz = Class.forName(type);
			for(Map<String, String> map : list){
				objList.add(ConfigUtils.parseObject(map, toClz));
			}
			return objList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
