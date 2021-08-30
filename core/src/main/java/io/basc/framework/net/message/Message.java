package io.basc.framework.net.message;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.MimeType;

public interface Message {
	Headers getHeaders();

	MimeType getContentType();
	
	long getContentLength();
	
	@Nullable
	default String getCharacterEncoding() {
		MimeType mimeType = getContentType();
		return mimeType == null ? null : mimeType.getCharsetName();
	}
}
