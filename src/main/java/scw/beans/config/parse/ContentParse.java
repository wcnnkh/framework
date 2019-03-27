package scw.beans.config.parse;

import java.io.File;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.common.FieldInfo;
import scw.common.utils.ConfigUtils;
import scw.common.utils.FileUtils;

public final class ContentParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo fieldInfo, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		List<String> list = FileUtils.getFileContentLineList(file, charset);
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

