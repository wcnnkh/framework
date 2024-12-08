package io.basc.framework.util.io;

import java.nio.charset.Charset;
import java.util.Optional;

public interface CharsetCapable {
	Charset getCharset();

	default String getCharsetName() {
		return getCharset().name();
	}

	public static Optional<String> getCharsetName(Object source) {
		if (source instanceof CharsetCapable) {
			return Optional.ofNullable(((CharsetCapable) source).getCharsetName());
		}
		return Optional.empty();
	}

	public static Optional<Charset> getCharset(Object source) {
		if (source instanceof CharsetCapable) {
			return Optional.ofNullable(((CharsetCapable) source).getCharset());
		}
		return Optional.empty();
	}
}
