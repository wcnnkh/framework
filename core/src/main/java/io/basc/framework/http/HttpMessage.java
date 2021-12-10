package io.basc.framework.http;

import io.basc.framework.net.message.Message;

public interface HttpMessage extends Message {
	HttpHeaders getHeaders();

	default long getContentLength() {
		return getHeaders().getContentLength();
	}

	default MediaType getContentType() {
		return getHeaders().getContentType();
	}
}