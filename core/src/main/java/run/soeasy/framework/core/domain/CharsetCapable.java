package run.soeasy.framework.core.domain;

import java.nio.charset.Charset;

public interface CharsetCapable {
	public static Charset getCharset(Object charset) {
		if (charset instanceof Charset) {
			return (Charset) charset;
		}

		if (charset instanceof CharsetCapable) {
			return ((CharsetCapable) charset).getCharset();
		}
		return null;
	}

	public static String getCharsetName(Object source) {
		if (source instanceof String) {
			return (String) source;
		}

		if (source instanceof CharsetCapable) {
			return ((CharsetCapable) source).getCharsetName();
		}
		return null;
	}

	Charset getCharset();

	default String getCharsetName() {
		return getCharset().name();
	}
}
