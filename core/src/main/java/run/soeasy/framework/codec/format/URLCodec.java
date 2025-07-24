package run.soeasy.framework.codec.format;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.MultipleCodec;

public class URLCodec implements MultipleCodec<String> {
	public static final URLCodec UTF_8 = new URLCodec(StandardCharsets.UTF_8);
	private final String charsetName;

	public URLCodec(@NonNull String charsetName) {
		this.charsetName = charsetName;
	}

	public URLCodec(@NonNull Charset charset) {
		this(charset.name());
	}

	public String encode(String source) throws CodecException {
		try {
			return URLEncoder.encode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new EncodeException(source, e);
		}
	}

	public String decode(String source) throws CodecException {
		try {
			return URLDecoder.decode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new DecodeException(source, e);
		}
	}

}
