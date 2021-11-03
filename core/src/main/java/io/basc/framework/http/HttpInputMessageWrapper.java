package io.basc.framework.http;

import io.basc.framework.net.message.InputMessageWrapper;

public class HttpInputMessageWrapper<W extends HttpInputMessage> extends InputMessageWrapper<W>
		implements HttpInputMessage {

	public HttpInputMessageWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public HttpHeaders getHeaders() {
		return wrappedTarget.getHeaders();
	}

	@Override
	public MediaType getContentType() {
		return wrappedTarget.getContentType();
	}
}
