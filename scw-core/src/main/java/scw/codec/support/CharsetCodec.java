package scw.codec.support;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.codec.AbstractCodec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.core.Constants;
import scw.util.StringOperations;

/**
 * 使用指定字符集进行编解码<br/>
 * 
 * @author shuchaowen
 *
 */
public class CharsetCodec extends AbstractCodec<String, byte[]> {
	public static final CharsetCodec UTF_8 = new CharsetCodec(Constants.UTF_8);

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

	public byte[] encode(String source) throws EncodeException {
		if (charset instanceof String) {
			try {
				return StringOperations.INSTANCE.getBytes(source,
						(String) charset);
			} catch (UnsupportedEncodingException e) {
				throw new EncodeException("charset=" + charset + ", source="
						+ source, e);
			}
		} else {
			return StringOperations.INSTANCE
					.getBytes(source, (Charset) charset);
		}
	}

	public String decode(byte[] source) throws DecodeException {
		if (charset instanceof String) {
			try {
				return StringOperations.INSTANCE.createString(source,
						(String) charset);
			} catch (UnsupportedEncodingException e) {
				throw new DecodeException("charset=" + charset, e);
			}
		} else {
			return StringOperations.INSTANCE.createString(source,
					(Charset) charset);
		}
	}

}
