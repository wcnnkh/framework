package scw.lang;

import java.io.UnsupportedEncodingException;

public final class Jdk5StringCodec implements StringCodec {
	public final String charsetName;

	public Jdk5StringCodec(String charsetName) {
		this.charsetName = charsetName;
	}

	public byte[] encode(String text) {
		try {
			return text.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String decode(byte[] bytes) {
		try {
			return new String(bytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
