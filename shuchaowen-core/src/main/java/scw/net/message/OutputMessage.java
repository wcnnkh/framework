package scw.net.message;

import java.io.OutputStream;

import scw.net.mime.MimeType;

public interface OutputMessage extends Message {
	void setContentType(MimeType contentType);

	void setContentLength(long contentLength);

	OutputStream getOutputStream();
}
