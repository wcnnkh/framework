package io.basc.framework.web.servlet.http;

import io.basc.framework.http.ContentDisposition;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.multipart.MultipartMessage;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

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
	public MimeType getContentType() {
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
