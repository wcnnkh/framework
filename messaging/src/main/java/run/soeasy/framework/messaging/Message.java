package run.soeasy.framework.messaging;

import java.nio.charset.Charset;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.io.CharsetCapable;
import run.soeasy.framework.core.io.MimeType;

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
		default MediaType getContentType() {
			return getSource().getContentType();
		}

		@Override
		default long getContentLength() {
			return getContentLength();
		}
	}

	Headers getHeaders();

	default long getContentLength() {
		return getHeaders().getContentLength();
	}

	default MediaType getContentType() {
		return getHeaders().getContentType();
	}

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
