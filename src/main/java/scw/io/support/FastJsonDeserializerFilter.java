package scw.io.support;

import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.io.DeserializerFilter;
import scw.io.DeserializerFilterChain;
import scw.io.IOUtils;

public class FastJsonDeserializerFilter implements DeserializerFilter{
	private String charsetName;
	
	public FastJsonDeserializerFilter(){
		this(Constants.DEFAULT_CHARSET_NAME);
	}
	
	public FastJsonDeserializerFilter(String charsetName){
		this.charsetName = charsetName;
	}
	
	public Object deserialize(Class<?> type, InputStream input, DeserializerFilterChain chain) throws IOException {
		String content = IOUtils.readContent(input, charsetName);
		if(StringUtils.isEmpty(content)){
			return null;
		}
		
		return JSON.parseObject(content, type, Feature.SupportNonPublicField);
	}
}
