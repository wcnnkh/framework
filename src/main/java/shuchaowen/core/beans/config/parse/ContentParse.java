package shuchaowen.core.beans.config.parse;

import java.io.File;
import java.util.List;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;

public class ContentParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo fieldInfo, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		List<String> list = ConfigUtils.getFileContentLineList(file, charset);
		if(String.class.isAssignableFrom(fieldInfo.getType())){
			StringBuilder sb = new StringBuilder();
			if(list != null){
				for(String str : list){
					sb.append(str);
				}
			}
			return sb.toString();
		}else if(List.class.isAssignableFrom(fieldInfo.getType())){
			return list;
		}
		return null;
	}
}

