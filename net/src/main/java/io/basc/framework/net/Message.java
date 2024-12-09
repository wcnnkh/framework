package io.basc.framework.net;

import java.nio.charset.Charset;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.CharsetCapable;

public interface Message extends CharsetCapable {
	@FunctionalInterface
	public static interface MessageWrapper<W extends Message> extends Message, CharsetCapableWrapper<W> {

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
		default MimeType getContentType() {
			return getSource().getContentType();
		}

		@Override
		default long getContentLength() {
			return getContentLength();
		}
	}

	Headers getHeaders();

	MimeType getContentType();

	long getContentLength();

	default Charset getCharset() {
		String name = getCharsetName();
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return Charset.forName(name);
	}

	@Override
	default String getCharsetName() {
		MimeType mimeType = getContentType();
		return mimeType == null ? null : mimeType.getCharsetName();
	}
}
