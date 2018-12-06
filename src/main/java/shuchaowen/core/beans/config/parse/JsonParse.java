package shuchaowen.core.beans.config.parse;

import java.io.File;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.FieldInfo;
import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.util.FileUtils;

/**
 * 将内容解析为json
 * @author shuchaowen
 *
 */
public class JsonParse implements ConfigParse{
	
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		String content = FileUtils.readerFileContent(file, charset).toString();
		if(JSONObject.class.isAssignableFrom(field.getType())){
			return JSONObject.parse(content);
		}else if(JSONArray.class.isAssignableFrom(field.getType())){
			return JSONArray.parse(content);
		}else if(String.class.isAssignableFrom(field.getType())){
			return content;
		}else{
			return JSONObject.parseObject(content, field.getType());
		}
	}

}
