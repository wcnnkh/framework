package run.soeasy.framework.codec.format;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;
import run.soeasy.framework.codec.DirectCodec;

public class URLCodec implements DirectCodec<String, String> {
	public static final URLCodec UTF_8 = new URLCodec(StandardCharsets.UTF_8);
	private final String charsetName;

	public URLCodec(@NonNull String charsetName) {
		this.charsetName = charsetName;
	}

	public URLCodec(@NonNull Charset charset) {
		this(charset.name());
	}

	public String encode(String source) {
		try {
			return URLEncoder.encode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(source, e);
		}
	}

	public String decode(String source) {
		try {
			return URLDecoder.decode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(source, e);
		}
	}

}
