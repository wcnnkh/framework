package io.basc.framework.util.codec.support;

import java.util.Locale;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;

/**
 * 大小写转换,默认是小写--&gt;大写
 * 
 * @author wcnnkh
 *
 */
public class LocaleCaseCodec implements Codec<String, String> {
	public static final LocaleCaseCodec DEFAULT = new LocaleCaseCodec();

	private final Locale locale;

	public LocaleCaseCodec() {
		this(null);
	}

	public LocaleCaseCodec(Locale locale) {
		this.locale = locale;
	}

	public String encode(String source) throws EncodeException {
		if (locale == null) {
			return source.toUpperCase();
		} else {
			return source.toUpperCase(locale);
		}
	}

	public String decode(String source) throws DecodeException {
		if (locale == null) {
			return source.toLowerCase();
		} else {
			return source.toLowerCase(locale);
		}
	}

}
