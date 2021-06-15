package scw.net.message.multipart;

import scw.http.HttpHeaders;
import scw.net.MimeType;

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
