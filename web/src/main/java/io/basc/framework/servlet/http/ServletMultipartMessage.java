package io.basc.framework.servlet.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.net.ContentDisposition;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.multipart.MultipartMessage;

public class ServletMultipartMessage implements MultipartMessage {
	private final Part part;

	public ServletMultipartMessage(Part part) {
		this.part = part;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return part.getInputStream();
	}

	private ServletPartHeaders headers;

	@Override
	public HttpHeaders getHeaders() {
		if (headers == null) {
			headers = new ServletPartHeaders(part);
		}
		return headers;
	}

	@Override
	public MediaType getContentType() {
		String contentType = part.getContentType();
		return MediaType.parseMediaType(contentType);
	}

	@Override
	public long getContentLength() {
		return part.getSize();
	}

	@Override
	public String getName() {
		return part.getName();
	}

	@Override
	public String getOriginalFilename() {
		ContentDisposition disposition = getHeaders().getContentDisposition();
		return disposition == null ? null : disposition.getFilename();
	}

	@Override
	public long getSize() {
		return part.getSize();
	}

}
