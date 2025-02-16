package io.basc.framework.http;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.function.Pipeline;
import lombok.NonNull;

public class DefaultHttpInputMessage implements HttpInputMessage {
	private HttpHeaders httpHeaders = new HttpHeaders();
	private InputMessage inputMessage;

	public DefaultHttpInputMessage(InputMessage inputMessage) {
		this.inputMessage = inputMessage;
		this.httpHeaders.putAll(inputMessage.getHeaders());
	}

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStream() {
		return inputMessage.getInputStream();
	}

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}
}
