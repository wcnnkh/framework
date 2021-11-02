package io.basc.framework.http;

import io.basc.framework.net.message.OutputMessageWrapper;

public class HttpOutputMessageWrapper<W extends HttpOutputMessage> extends OutputMessageWrapper<W>
		implements HttpOutputMessage {

	public HttpOutputMessageWrapper(W wrappedTarget) {
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
