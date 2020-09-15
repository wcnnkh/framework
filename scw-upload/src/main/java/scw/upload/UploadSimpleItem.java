package scw.upload;

import java.io.InputStream;

import scw.http.HttpHeaders;
import scw.io.UnsafeByteArrayInputStream;

public class UploadSimpleItem implements UploadItem {
	private final HttpHeaders headers = new HttpHeaders();
	private final String fileName;
	private final InputStream body;
	private final long size;

	public UploadSimpleItem(String fileName, byte[] body) {
		this(fileName, new UnsafeByteArrayInputStream(body), body.length);
	}

	public UploadSimpleItem(String fileName, InputStream body, long size) {
		this.fileName = fileName;
		this.body = body;
		this.size = size;
	}

	public String getName() {
		return fileName;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public InputStream getBody() {
		return body;
	}

	public long size() {
		return size;
	}

}
