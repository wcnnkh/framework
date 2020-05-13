package scw.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import scw.net.MimeType;
import scw.net.message.Headers;

public class ServerRequestWrapper<T extends ServerRequest> implements ServerRequest {
	protected final T targetRequest;

	public ServerRequestWrapper(T targetRequest) {
		this.targetRequest = targetRequest;
	}

	public InputStream getBody() throws IOException {
		return targetRequest.getBody();
	}

	public Headers getHeaders() {
		return targetRequest.getHeaders();
	}

	public MimeType getContentType() {
		return targetRequest.getContentType();
	}

	public long getContentLength() {
		return targetRequest.getContentLength();
	}

	public String getController() {
		return targetRequest.getController();
	}

	public String getRawContentType() {
		return targetRequest.getRawContentType();
	}

	public String getContextPath() {
		return targetRequest.getContextPath();
	}

	public String getCharacterEncoding() {
		return targetRequest.getCharacterEncoding();
	}

	public BufferedReader getReader() throws IOException {
		return targetRequest.getReader();
	}

	public InetSocketAddress getLocalAddress() {
		return targetRequest.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress() {
		return targetRequest.getRemoteAddress();
	}

}
