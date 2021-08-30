package io.basc.framework.codec.support;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.MultipleCodec;
import io.basc.framework.lang.Nullable;

import java.util.Locale;

/**
 * 大小写转换,默认是小写->大写
 * 
 * @author shuchaowen
 *
 */
public class CaseCodec implements MultipleCodec<String> {
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
