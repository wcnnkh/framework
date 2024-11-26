package io.basc.framework.util.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.basc.framework.io.IOUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import io.basc.framework.util.codec.Encoder;
import io.basc.framework.util.codec.encode.HmacMD5;
import io.basc.framework.util.codec.encode.HmacSHA1;

/**
 * 使用指定字符集进行编解码
 * 
 * @author wcnnkh
 *
 */
public class CharsetCodec implements ToBytesCodec<String> {
	/**
	 * @see Charset#defaultCharset()
	 */
	public static final CharsetCodec DEFAULT = new CharsetCodec(Charset.defaultCharset());

	public static final CharsetCodec UTF_8 = new CharsetCodec(Constants.UTF_8);

	public static final CharsetCodec ISO_8859_1 = new CharsetCodec(Constants.ISO_8859_1);

	public static final CharsetCodec US_ASCII = new CharsetCodec(Constants.US_ASCII);

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
			throw new EncodeException("charset=" + charset + ", source=" + source, e);
		}
	}

	@Override
	public void encode(String source, OutputStream target) throws IOException, EncodeException {
		byte[] v = encode(source);
		if (v == null) {
			return;
		}
		target.write(v);
	}

	@Override
	public String decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		return decode(IOUtils.copyToByteArray(source, bufferSize));
	}

	public String decode(byte[] source) throws DecodeException {
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
			throw new EncodeException("charset=" + charset + ", source=" + source, e);
		}
	}

	public Codec<String, String> gzip(Codec<byte[], String> codec) {
		return to(Gzip.DEFAULT).to(codec);
	}

	public Codec<String, String> gzip() {
		return gzip(HexCodec.DEFAULT);
	}

	public Encoder<String, String> toHmacMD5(String secretKey) {
		return toEncoder(new HmacMD5(encode(secretKey)).toEncoder(HexCodec.DEFAULT));
	}

	public Encoder<String, String> toHmacSHA1(String secretKey) {
		return toEncoder(new HmacSHA1(encode(secretKey)).toEncoder(HexCodec.DEFAULT));
	}

	public static CharsetCodec charset(Charset charset) {
		return new CharsetCodec(charset);
	}

	public static CharsetCodec charset(String charsetName) {
		return new CharsetCodec(charsetName);
	}
}
