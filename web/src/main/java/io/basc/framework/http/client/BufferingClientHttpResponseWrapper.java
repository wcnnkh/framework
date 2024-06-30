package io.basc.framework.http.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.io.IOUtils;

public final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

	private final ClientHttpResponse response;

	private byte[] body;

	BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
		this.response = response;
	}

	public HttpStatus getStatusCode() throws IOException {
		return this.response.getStatusCode();
	}

	public int getRawStatusCode() throws IOException {
		return this.response.getRawStatusCode();
	}

	public String getStatusText() throws IOException {
		return this.response.getStatusText();
	}

	public HttpHeaders getHeaders() {
		return this.response.getHeaders();
	}

	public InputStream getInputStream() throws IOException {
		if (this.body == null) {
			this.body = IOUtils.copyToByteArray(this.response.getInputStream());
		}
		return new ByteArrayInputStream(this.body);
	}

	public void close() {
		this.response.close();
	}
}
