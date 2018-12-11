package shuchaowen.connection.http.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.connection.Request;
import shuchaowen.connection.RequestEntity;
import shuchaowen.connection.http.entity.parameter.FormParameter;

public class FormRequestEntity extends ArrayList<FormParameter> implements RequestEntity{
	private static final long serialVersionUID = 1L;
	private static final CharBuffer JION = CharBuffer.allocate(1).put('&');
	private final Charset charset;

	public FormRequestEntity(Charset charset) {
		this.charset = charset;
	}
	
	public FormRequestEntity addParameter(String key, Object value){
		if(key == null || value == null || key.length() == 0){
			return this;
		}
		
		add(new FormParameter(key, value.toString(), charset));
		return this;
	}

	public void write(Request request) throws IOException {
		if(request.getRequestProperty("Charset") == null){
			request.setRequestProperty("Charset", charset.name());
		}
		
		if(request.getRequestProperty("Content-Type") == null){
			request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charset.name());
		}
		
		OutputStream out = request.getOutputStream();
		Iterator<FormParameter> iterator = iterator();
		while(iterator.hasNext()){
			iterator.next().write(out);
			if(iterator.hasNext()){
				out.write(charset.encode(JION).array());
			}
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
