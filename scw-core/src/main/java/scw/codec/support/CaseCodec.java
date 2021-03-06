package scw.codec.support;

import java.util.Locale;

import scw.codec.AbstractCodec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.lang.Nullable;

/**
 * 大小写转换,默认是小写->大写
 * 
 * @author shuchaowen
 *
 */
public class CaseCodec extends AbstractCodec<String, String> {
	public static final CaseCodec DEFAULT = new CaseCodec();
	
	private final Locale locale;

	public CaseCodec() {
		this(null);
	}

	public CaseCodec(@Nullable Locale locale) {
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
