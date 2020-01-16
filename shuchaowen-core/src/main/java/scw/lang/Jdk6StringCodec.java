package scw.lang;

import java.nio.charset.Charset;

public final class Jdk6StringCodec implements StringCodec {
	public final Charset charset;

	public Jdk6StringCodec(String charsetName) {
		this.charset = Charset.forName(charsetName);
	}

	public Jdk6StringCodec(Charset charset) {
		this.charset = charset;
	}

	public byte[] encode(String text) {
		return text.getBytes(charset);
	}

	public String decode(byte[] bytes) {
		return new String(bytes, charset);
	}
}
