package run.soeasy.framework.codec.format;

import java.util.Locale;

import run.soeasy.framework.codec.DirectCodec;

/**
 * 大小写转换,默认是小写--&gt;大写
 * 
 * @author soeasy.run
 *
 */
public class LocaleCaseCodec implements DirectCodec<String, String> {
	public static final LocaleCaseCodec DEFAULT = new LocaleCaseCodec();

	private final Locale locale;

	public LocaleCaseCodec() {
		this(null);
	}

	public LocaleCaseCodec(Locale locale) {
		this.locale = locale;
	}

	public String encode(String source) {
		if (locale == null) {
			return source.toUpperCase();
		} else {
			return source.toUpperCase(locale);
		}
	}

	public String decode(String source) {
		if (locale == null) {
			return source.toLowerCase();
		} else {
			return source.toLowerCase(locale);
		}
	}

}
