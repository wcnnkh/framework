package scw.http;

import scw.net.message.Message;

public interface HttpMessage extends Message{
	HttpHeaders getHeaders();
	
	default long getContentLength() {
		return getHeaders().getContentLength();
	}

	default MediaType getContentType() {
		return getHeaders().getContentType();
	}
}