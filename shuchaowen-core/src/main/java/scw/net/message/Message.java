package scw.net.message;

import scw.net.mime.MimeType;

public interface Message {
	MimeType getContentType();

	long getContentLength();
}
