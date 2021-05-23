package scw.net.message;

import java.io.IOException;
import java.io.OutputStream;

import scw.net.MimeType;

public interface OutputMessage extends Message {
	void setContentType(MimeType contentType);

	void setContentLength(long contentLength);

	OutputStream getBody() throws IOException;

	default void setCharacterEncoding(String charsetName) {
		MimeType mimeType = getContentType();
		if (mimeType == null) {
			return;
		}

		setContentType(new MimeType(mimeType, charsetName));
	}
}
