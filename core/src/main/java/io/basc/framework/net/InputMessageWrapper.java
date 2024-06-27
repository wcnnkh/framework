package io.basc.framework.net;

import io.basc.framework.io.InputStreamSourceWrapper;

import java.io.IOException;

public class InputMessageWrapper<I extends InputMessage> extends InputStreamSourceWrapper<I> implements InputMessage {

	public InputMessageWrapper(I wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Headers getHeaders() {
		return wrappedTarget.getHeaders();
	}

	@Override
	public MimeType getContentType() {
		return wrappedTarget.getContentType();
	}

	@Override
	public long getContentLength() {
		return wrappedTarget.getContentLength();
	}

	@Override
	public String getString() throws IOException {
		return wrappedTarget.getString();
	}

	@Override
	public String getCharacterEncoding() {
		return wrappedTarget.getCharacterEncoding();
	}
}
