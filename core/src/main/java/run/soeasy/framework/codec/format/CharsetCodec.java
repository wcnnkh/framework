package run.soeasy.framework.codec.format;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.codec.DirectCodec;
import run.soeasy.framework.codec.commons.ToBinaryCodec;
import run.soeasy.framework.codec.crypto.MAC;

/**
 * 使用指定字符集进行编解码
 * 
 * @author soeasy.run
 *
 */
public class CharsetCodec implements DirectCodec<String, byte[]>, ToBinaryCodec<String> {
	/**
	 * @see Charset#defaultCharset()
	 */
	public static final CharsetCodec DEFAULT = new CharsetCodec(Charset.defaultCharset());

	public static final CharsetCodec UTF_8 = new CharsetCodec(StandardCharsets.UTF_8);

	public static final CharsetCodec ISO_8859_1 = new CharsetCodec(StandardCharsets.ISO_8859_1);

	public static final CharsetCodec US_ASCII = new CharsetCodec(StandardCharsets.US_ASCII);

	private final Object charset;

	public CharsetCodec(String charsetName) {
		this.charset = charsetName;
	}

	public CharsetCodec(Charset charset) {
		this.charset = charset;
	}

	public String getCharsetName() {
		if (charset instanceof Charset) {
			return ((Charset) charset).name();
		} else {
			return (String) charset;
		}
	}

	public Charset getCharset() {
		if (charset instanceof Charset) {
			return (Charset) charset;
		} else {
			return Charset.forName((String) charset);
		}
	}

	public byte[] encode(String source) {
		if (source == null) {
			return null;
		}

		try {
			if (charset instanceof Charset) {
				return source.getBytes((Charset) charset);
			} else {
				return source.getBytes((String) charset);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("charset=" + charset + ", source=" + source, e);
		}
	}

	public String decode(byte[] source) {
		if (source == null) {
			return null;
		}

		try {
			if (charset instanceof Charset) {
				return new String(source, (Charset) charset);
			} else {
				return new String(source, (String) charset);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("charset=" + charset + ", source=" + source, e);
		}
	}

	public Coder<String, byte[]> toMac(String algorithm, String secretKey) {
		return encoder().to(new MAC(algorithm, encode(secretKey)));
	}

	public static CharsetCodec charset(Charset charset) {
		return new CharsetCodec(charset);
	}

	public static CharsetCodec charset(String charsetName) {
		return new CharsetCodec(charsetName);
	}
}
