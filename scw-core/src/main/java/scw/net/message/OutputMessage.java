package scw.net.message;

import scw.io.OutputStreamSource;
import scw.net.MimeType;

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
