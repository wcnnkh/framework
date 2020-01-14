package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

public interface InputMessage extends Message {
	InputStream getBody() throws IOException;

	byte[] toByteArray();

	/**
	 * 使用指定的字符集转换为字符串
	 * 
	 * @param charsetName
	 * @return
	 */
	String toString(String charsetName);

	/**
	 * 使用默认的字符集转换成字符串
	 * 
	 * @return
	 */
	@Override
	String toString();
}
