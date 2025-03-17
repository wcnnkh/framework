package run.soeasy.framework.net.multipart;

import run.soeasy.framework.net.ContentDisposition;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.util.io.UnsafeByteArrayInputStream;

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
