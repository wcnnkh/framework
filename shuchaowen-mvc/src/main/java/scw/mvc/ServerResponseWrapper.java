package scw.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import scw.net.MimeType;
import scw.net.message.Headers;

public class ServerResponseWrapper<T extends ServerResponse> implements ServerResponse {
	protected final T targetResponse;
	
	public ServerResponseWrapper(T targetResponse){
		this.targetResponse = targetResponse;
	}
	
	public void setContentType(MimeType contentType) {
		targetResponse.setContentType(contentType);
	}

	public void setContentLength(long contentLength) {
		targetResponse.setContentLength(contentLength);
	}

	public OutputStream getBody() throws IOException {
		return targetResponse.getBody();
	}

	public Headers getHeaders() {
		return targetResponse.getHeaders();
	}

	public MimeType getContentType() {
		return targetResponse.getContentType();
	}

	public long getContentLength() {
		return targetResponse.getContentLength();
	}

	public void flush() throws IOException {
		targetResponse.flush();
	}

	public String getRawContentType() {
		return targetResponse.getRawContentType();
	}

	public void setContentType(String contentType) {
		targetResponse.setContentType(contentType);
	}
	
	public boolean isCommitted() {
		return targetResponse.isCommitted();
	}

	public String getCharacterEncoding() {
		return targetResponse.getCharacterEncoding();
	}

	public PrintWriter getWriter() throws IOException {
		return targetResponse.getWriter();
	}
	
}
