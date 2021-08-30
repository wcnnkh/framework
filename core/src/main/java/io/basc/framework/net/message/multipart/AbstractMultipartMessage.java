package io.basc.framework.net.message.multipart;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.net.MimeType;

public abstract class AbstractMultipartMessage implements MultipartMessage {
	private final HttpHeaders httpHeaders = new HttpHeaders();
	private final String name;

	public AbstractMultipartMessage(String name) {
		this.name = name;
	}

	@Override
	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	@Override
	public MimeType getContentType() {
		return httpHeaders.getContentType();
	}

	@Override
	public long getContentLength() {
		return httpHeaders.getContentLength();
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
