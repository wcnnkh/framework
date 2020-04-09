package scw.compatible;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.core.instance.annotation.Configuration;

@Configuration(order=Jdk6StringOperations.CONFIGURATION_ORDER)
public class Jdk6StringOperations implements StringOperations{
	public static final int CONFIGURATION_ORDER = 6;

	public String createString(byte[] bytes, String charsetName)
			throws UnsupportedEncodingException {
		return new String(bytes, charsetName);
	}

	public String createString(byte[] bytes, Charset charset) {
		return new String(bytes, charset);
	}

	public byte[] getBytes(String text, String charsetName)
			throws UnsupportedEncodingException {
		return text.getBytes(charsetName);
	}

	public byte[] getBytes(String text, Charset charset) {
		return text.getBytes(charset);
	}
}
