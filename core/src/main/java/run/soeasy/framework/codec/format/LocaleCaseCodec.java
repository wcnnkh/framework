package run.soeasy.framework.codec.format;

import java.util.Locale;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;

/**
 * 大小写转换,默认是小写--&gt;大写
 * 
 * @author soeay.run
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
