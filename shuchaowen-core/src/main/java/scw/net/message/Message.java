package scw.net.message;

import scw.net.mime.MimeType;

public interface Message {
	Headers getHeaders();
	
	MimeType getContentType();

	long getContentLength();
}
