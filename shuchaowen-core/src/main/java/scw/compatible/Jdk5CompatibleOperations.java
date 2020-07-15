package scw.compatible;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.lang.NestedRuntimeException;

public class Jdk5CompatibleOperations implements StringOperations {
	public static final int CONFIGURATION_ORDER = 5;

	public String createString(byte[] bytes, String charsetName)
			throws UnsupportedEncodingException {
		return new String(bytes, charsetName);
	}

	public String createString(byte[] bytes, Charset charset) {
		try {
			return createString(bytes, charset.name());
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}

	public byte[] getBytes(String text, String charsetName)
			throws UnsupportedEncodingException {
		return text.getBytes(charsetName);
	}

	public byte[] getBytes(String text, Charset charset) {
		try {
			return getBytes(text, charset.name());
		} catch (UnsupportedEncodingException e) {
			throw new NestedRuntimeException(e);
		}
	}
}
