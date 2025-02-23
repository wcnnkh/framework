package io.basc.framework.http;

import io.basc.framework.net.Message;

public interface HttpMessage extends Message {
	public static interface HttpMessageWrapper<W extends HttpMessage> extends HttpMessage, MessageWrapper<W> {
		@Override
		default HttpHeaders getHeaders() {
			return getSource().getHeaders();
		}
	}

	HttpHeaders getHeaders();
}