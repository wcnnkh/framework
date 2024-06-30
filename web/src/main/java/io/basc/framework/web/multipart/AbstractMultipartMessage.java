package io.basc.framework.web.multipart;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.net.MimeType;

public abstract class AbstractMultipartMessage implements MultipartMessage {
	private final HttpHeaders headers = new HttpHeaders();
	private final String name;

	public AbstractMultipartMessage(String name) {
		this.name = name;
	}

	@Override
	public HttpHeaders getHeaders() {
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
