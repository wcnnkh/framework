package run.soeasy.framework.core.domain;

import java.nio.charset.Charset;

@FunctionalInterface
public interface CharsetCapableWrapper<W extends CharsetCapable> extends CharsetCapable, Wrapper<W> {
	@Override
	default Charset getCharset() {
		return getSource().getCharset();
	}

	@Override
	default String getCharsetName() {
		return getSource().getCharsetName();
	}
}