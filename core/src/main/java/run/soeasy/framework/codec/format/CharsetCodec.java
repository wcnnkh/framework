package run.soeasy.framework.codec.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.binary.Gzip;
import run.soeasy.framework.codec.binary.ToBinaryCodec;
import run.soeasy.framework.codec.crypto.MAC;
import run.soeasy.framework.io.IOUtils;

/**
 * 使用指定字符集进行编解码
 * 
 * @author soeasy.run
 *
 */
public class CharsetCodec implements ToBinaryCodec<String> {
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

	public byte[] encode(String source) throws CodecException {
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
	public void encode(String source, @NonNull OutputStream target) throws IOException, CodecException {
		byte[] v = encode(source);
		if (v == null) {
			return;
		}
		target.write(v);
	}

	@Override
	public String decode(InputStream source, int bufferSize) throws IOException, CodecException {
		return decode(IOUtils.toByteArray(source));
	}

	public String decode(byte[] source) throws CodecException {
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
			throw new DecodeException("charset=" + charset + ", source=" + source, e);
		}
	}

	public Codec<String, String> gzip(Codec<byte[], String> codec) {
		return to(Gzip.DEFAULT).to(codec);
	}

	public Codec<String, String> gzip() {
		return gzip(HexCodec.DEFAULT);
	}

	public Encoder<String, byte[]> toMac(String algorithm, String secretKey) {
		return toEncoder(new MAC(algorithm, encode(secretKey)));
	}

	public static CharsetCodec charset(Charset charset) {
		return new CharsetCodec(charset);
	}

	public static CharsetCodec charset(String charsetName) {
		return new CharsetCodec(charsetName);
	}
}
