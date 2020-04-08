package scw.net.client.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import scw.io.StreamUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;
import scw.net.message.AbstractInputMessage;

final class BufferingClientHttpResponseWrapper extends AbstractInputMessage implements ClientHttpResponse {

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

	public InputStream getBody() throws IOException {
		if (this.body == null) {
			this.body = StreamUtils.copyToByteArray(this.response.getBody());
		}
		return new ByteArrayInputStream(this.body);
	}

	public void close() {
		this.response.close();
	}
}
