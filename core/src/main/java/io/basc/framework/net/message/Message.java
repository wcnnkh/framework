package io.basc.framework.net.message;

import java.nio.charset.Charset;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.StringUtils;

public interface Message {
	Headers getHeaders();

	MimeType getContentType();

	long getContentLength();

	/**
	 * 为了兼容旧的版本，默认的实现是调用{@see #getCharacterEncoding()}
	 * 
	 * @return
	 */
	@Nullable
	default Charset getCharset() {
		String name = getCharacterEncoding();
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return Charset.forName(name);
	}

	@Nullable
	default String getCharacterEncoding() {
		MimeType mimeType = getContentType();
		return mimeType == null ? null : mimeType.getCharsetName();
	}
}
