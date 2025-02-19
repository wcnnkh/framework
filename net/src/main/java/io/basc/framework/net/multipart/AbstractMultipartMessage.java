package io.basc.framework.net.multipart;

import io.basc.framework.net.Headers;
import io.basc.framework.net.MimeType;

public abstract class AbstractMultipartMessage implements MultipartMessage {
	private final Headers headers = new Headers(false);
	private final String name;

	public AbstractMultipartMessage(String name) {
		this.name = name;
	}

	@Override
	public Headers getHeaders() {
		return headers;
	}

	@Override
	public MimeType getContentType() {
		return headers.getContentType();
	}

	@Override
	public long getContentLength() {
		return headers.getContentLength();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getSize() {
		return getContentLength();
	}
}
