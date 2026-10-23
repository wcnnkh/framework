package run.soeasy.framework.codec.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;
import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.codec.crypto.MAC;
import run.soeasy.framework.codec.io.DirectSerializer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.CharsetAware;

/**
 * 使用指定字符集进行编解码
 * 
 * @author soeasy.run
 *
 */
public class CharsetCodec implements DirectSerializer<String>, CharsetAware {
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

	@Override
	public String deserialize(@NonNull InputStream input) throws IOException {
		byte[] data = IOUtils.binary().toByteArray(input);
		if (charset instanceof Charset) {
			return new String(data, (Charset) charset);
		} else {
			return new String(data, (String) charset);
		}
	}

	@Override
	public void serialize(String source, @NonNull OutputStream target) throws IOException {
		byte[] data;
		if (charset instanceof Charset) {
			data = source.getBytes((Charset) charset);
		} else {
			data = source.getBytes((String) charset);
		}
		target.write(data);
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
