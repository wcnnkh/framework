package scw.net.http.entity;

import java.io.IOException;
import java.nio.charset.Charset;

import scw.net.Request;
import scw.net.RequestEntity;

public final class BodyRequestEntity implements RequestEntity {
	private byte[] body;

	public BodyRequestEntity(String body, Charset charset) {
		this.body = body.getBytes(charset);
	}

	public BodyRequestEntity(byte[] body) {
		this.body = body;
	}

	public void write(Request request) throws IOException {
		request.getOutputStream().write(body);
	}
}
