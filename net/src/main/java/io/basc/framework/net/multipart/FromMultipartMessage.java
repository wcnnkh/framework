package io.basc.framework.net.multipart;

import io.basc.framework.http.ContentDisposition;
import io.basc.framework.http.MediaType;
import io.basc.framework.util.io.UnsafeByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;

public class FromMultipartMessage extends AbstractMultipartMessage {
	private final byte[] body;

	public FromMultipartMessage(String name, byte[] body) {
		super(name);
		this.body = body;
		getHeaders().setContentDisposition(ContentDisposition.builder("form-data").name(name).build());
		getHeaders().setContentType(new MediaType(MediaType.TEXT_HTML));
		getHeaders().setContentLength(body.length);
	}

	@Override
	public String getOriginalFilename() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new UnsafeByteArrayInputStream(body);
	}
}
