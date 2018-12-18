package shuchaowen.common.net.http.entity.parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class FormParameter implements Parameter{
	private final String key;
	private final String value;
	private final Charset charset;
	
	public FormParameter(String key, String value, Charset charset){
		this.key = key;
		this.value = value;
		this.charset = charset;
	}

	public void write(OutputStream out) throws IOException {
		if(key == null || value == null || key.length() == 0){
			return ;
		}
		
		String value = URLEncoder.encode(this.value, charset.name());
		CharBuffer charBuffer = CharBuffer.allocate(key.length() + 1 + value.length());
		charBuffer.put(key);
		charBuffer.put('=');
		charBuffer.put(value);
		out.write(charset.encode(charBuffer).array());
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
