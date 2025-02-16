package io.basc.framework.http;

import io.basc.framework.net.Message;

public interface HttpMessage extends Message {
	public static interface HttpMessageWrapper<W extends HttpMessage> extends HttpMessage, MessageWrapper<W> {
		@Override
		default HttpHeaders getHeaders() {
			return getSource().getHeaders();
		}

		@Override
		default long getContentLength() {
			return getSource().getContentLength();
		}

		@Override
		default MediaType getContentType() {
			return getSource().getContentType();
		}
	}

	HttpHeaders getHeaders();

	default long getContentLength() {
		return getHeaders().getContentLength();
	}

	default MediaType getContentType() {
		return getHeaders().getContentType();
	}
}