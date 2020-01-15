package scw.net.message;

import scw.net.MimeType;

public interface Message {
	Headers getHeaders();
	
	MimeType getContentType();

	long getContentLength();
}
