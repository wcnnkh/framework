package shuchaowen.connection.http.entity.parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class FormParameter implements Parameter{
	private final static char EQUAL = '=';
	private final String key;
	private final String value;
	private final Charset charset;
	
	public FormParameter(String key, String value, Charset charset){
		this.key = key;
		this.value = value;
		this.charset = charset;
	}

	public void write(OutputStream out) throws IOException {
		char[] keyChars = key.toCharArray();
		char[] valueChars = URLEncoder.encode(value, charset.name()).toCharArray();
		CharBuffer charBuffer = CharBuffer.allocate(keyChars.length + 1 + valueChars.length);
		charBuffer.put(key.toCharArray());
		charBuffer.put(EQUAL);
		charBuffer.put(URLEncoder.encode(value, charset.name()).toCharArray());
		ByteBuffer byteBuffer = charset.encode(charBuffer);
		out.write(byteBuffer.array());
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
