package scw.web.servlet.http.multipart;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import scw.http.ContentDisposition;
import scw.http.HttpHeaders;
import scw.http.MediaType;
import scw.net.MimeType;
import scw.net.message.multipart.MultipartMessage;

public class PartMultipartMessage implements MultipartMessage {
	private final Part part;

	public PartMultipartMessage(Part part) {
		this.part = part;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return part.getInputStream();
	}

	private PartHeaders headers;

	@Override
	public HttpHeaders getHeaders() {
		if (headers == null) {
			headers = new PartHeaders(part);
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
