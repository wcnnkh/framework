package run.soeasy.framework.messaging;

import java.nio.charset.Charset;

import run.soeasy.framework.core.domain.CharsetCapableWrapper;

@FunctionalInterface
public interface MessageWrapper<W extends Message> extends Message, CharsetCapableWrapper<W> {

	@Override
	default Charset getCharset() {
		return getSource().getCharset();
	}

	@Override
	default String getCharsetName() {
		return getSource().getCharsetName();
	}

	@Override
	default Headers getHeaders() {
		return getSource().getHeaders();
	}

	@Override
	default MediaType getContentType() {
		return getSource().getContentType();
	}

	@Override
	default long getContentLength() {
		return getContentLength();
	}
}