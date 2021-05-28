package scw.net.message;

import scw.lang.Nullable;
import scw.net.MimeType;

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
