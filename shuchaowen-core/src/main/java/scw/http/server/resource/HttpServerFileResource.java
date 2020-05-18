package scw.http.server.resource;

import java.io.IOException;
import java.util.Enumeration;

import scw.io.FileSystemResource;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.net.message.Headers;

public class HttpServerFileResource extends FileSystemResource implements HttpServerResource {

	public HttpServerFileResource(String path) {
		super(path);
	}

	public String getHeader(String name) {
		return null;
	}

	public Enumeration<String> getHeaderNames() {
		return null;
	}

	public Enumeration<String> getHeaders(String name) {
		return null;
	}

	public Headers getHeaders() {
		return null;
	}

	public String getRawContentType() {
		return getContentType().toString();
	}

	public MimeType getContentType() {
		return FileMimeTypeUitls.getMimeType(this);
	}

	public long getContentLength() {
		try {
			return contentLength();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
