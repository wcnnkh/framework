package shuchaowen.core.http.client.parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FormParameter implements Parameter{
	private String charsetName;
	private StringBuilder sb = new StringBuilder();
	
	public FormParameter(String charsetName){
		this.charsetName = charsetName;
	}
	
	public void addParameter(String name, String value){
		if(name == null || value == null){
			return ;
		}
		
		String encode_value = null;
		try {
			encode_value = URLEncoder.encode(value, charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(encode_value == null){
			return ;
		}
		
		if(sb.length() != 0){
			sb.append("&");
		}
		
		sb.append(name);
		sb.append("=");
		sb.append(value);
	}
	
	public void wrapper(OutputStream outputStream) throws IOException {
		outputStream.write(toString().getBytes(charsetName));
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
}
