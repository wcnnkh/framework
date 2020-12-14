package scw.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.lang.NestedRuntimeException;

public class Jdk5StringOperations implements StringOperations {

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
