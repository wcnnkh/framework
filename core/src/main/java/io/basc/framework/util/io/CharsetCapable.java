package io.basc.framework.util.io;

import java.nio.charset.Charset;
import java.util.Optional;

import io.basc.framework.util.Wrapper;

public interface CharsetCapable {
	@FunctionalInterface
	public static interface CharsetCapableWrapper<W extends CharsetCapable> extends CharsetCapable, Wrapper<W> {
		@Override
		default Charset getCharset() {
			return getSource().getCharset();
		}

		@Override
		default String getCharsetName() {
			return getSource().getCharsetName();
		}
	}

	public static Optional<Charset> getCharset(Object source) {
		if (source instanceof CharsetCapable) {
			return Optional.ofNullable(((CharsetCapable) source).getCharset());
		}
		return Optional.empty();
	}

	public static Optional<String> getCharsetName(Object source) {
		if (source instanceof CharsetCapable) {
			return Optional.ofNullable(((CharsetCapable) source).getCharsetName());
		}
		return Optional.empty();
	}

	Charset getCharset();

	default String getCharsetName() {
		return getCharset().name();
	}
}
