package shuchaowen.common.net.http.entity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.net.Request;
import shuchaowen.common.net.RequestEntity;
import shuchaowen.common.net.http.entity.parameter.FormParameter;

public class FormRequestEntity implements RequestEntity{
	private final Charset charset;
	private List<FormParameter> list;

	public FormRequestEntity(Charset charset) {
		this.charset = charset;
	}
	
	public Charset getCharset() {
		return charset;
	}

	public FormRequestEntity addParameter(String key, Object value){
		if(key == null || value == null || key.length() == 0){
			return this;
		}
		
		if(list == null){
			list = new ArrayList<FormParameter>();
		}
		
		list.add(new FormParameter(key, value.toString(), charset));
		return this;
	}
	
	public Iterator<FormParameter> iterator(){
		return list == null? (new ArrayList<FormParameter>()).iterator():list.iterator();
	}
	
	public void write(Request request) throws IOException {
		if(request.getRequestProperty("Charset") == null){
			request.setRequestProperty("Charset", charset.name());
		}
		
		if(request.getRequestProperty("Content-Type") == null){
			request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charset.name());
		}
		
		if(list != null){
			Iterator<FormParameter> iterator = list.iterator();
			StringBuilder sb = new StringBuilder();
			while(iterator.hasNext()){
				FormParameter formParameter = iterator.next();
				sb.append(formParameter.getKey());
				sb.append("=");
				sb.append(URLEncoder.encode(formParameter.getValue(), charset.name()));
				if(iterator.hasNext()){
					sb.append("&");
				}
			}
			request.getOutputStream().write(sb.toString().getBytes(charset));
		}
	}
	
	public static FormRequestEntity wrapper(Map<String, ?> parameterMap, Charset charset){
		FormRequestEntity formRequestEntity = new FormRequestEntity(charset);
		for(Entry<String, ?> entry : parameterMap.entrySet()){
			formRequestEntity.addParameter(entry.getKey(), entry.getValue());
		}
		return formRequestEntity;
	}
}
