package scw.beans.config.parse;

import java.io.File;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ConfigUtils;
import scw.io.FileUtils;

public final class ContentParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		List<String> list = FileUtils.getFileContentLineList(file, charset);
		if(String.class.isAssignableFrom(fieldDefinition.getField().getType())){
			StringBuilder sb = new StringBuilder();
			if(list != null){
				for(String str : list){
					sb.append(str);
				}
			}
			return sb.toString();
		}else if(List.class.isAssignableFrom(fieldDefinition.getField().getType())){
			return list;
		}
		return null;
	}
}

