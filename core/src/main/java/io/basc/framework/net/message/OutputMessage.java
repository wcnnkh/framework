package io.basc.framework.net.message;

import io.basc.framework.io.OutputStreamSource;
import io.basc.framework.net.MimeType;

public interface OutputMessage extends Message, OutputStreamSource {
	void setContentType(MimeType contentType);

	void setContentLength(long contentLength);

	default void setCharacterEncoding(String charsetName) {
		MimeType mimeType = getContentType();
		if (mimeType == null) {
			return;
		}

		setContentType(new MimeType(mimeType, charsetName));
	}
}
