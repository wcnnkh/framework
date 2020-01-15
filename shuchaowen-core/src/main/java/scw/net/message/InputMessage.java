package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

public interface InputMessage extends Message {
	InputStream getBody() throws IOException;

	byte[] toByteArray() throws IOException;

	/**
	 * 使用指定的字符集转换为字符串
	 * 
	 * @param charsetName
	 * @return
	 */
	String convertToString(String charsetName) throws IOException;

	/**
	 * 使用默认的字符集转换成字符串
	 * 
	 * @return
	 */
	String convertToString() throws IOException;
}
