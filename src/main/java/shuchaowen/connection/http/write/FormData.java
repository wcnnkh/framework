package shuchaowen.connection.http.write;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import shuchaowen.connection.Write;

public class FormData implements Write{
	private final String charsetName;
	private StringBuilder sb;
	
	public FormData(String charsetName){
		this.charsetName = charsetName;
	}
	
	public FormData addEncodeParameter(String key, Object value) throws UnsupportedEncodingException{
		if(value == null){
			return this;
		}
		
		return addParameter(key, encode(value, charsetName));
		}
	
	public FormData addParameter(String key, String value){
		if(value == null){
			return this;
		}
		
		if(sb == null){
			sb = new StringBuilder();
		}else{
			sb.append("&");
		}
		
		sb.append(key);
		sb.append("=");
		sb.append(value);
		return this;
	}

	public void write(OutputStream outputStream) throws IOException {
		if(sb != null){
			outputStream.write(sb.toString().getBytes(charsetName));
		}
	}
	
	public static String encode(Object value, String charsetName) throws UnsupportedEncodingException{
		return URLEncoder.encode(value.toString(), charsetName);
	}
}
