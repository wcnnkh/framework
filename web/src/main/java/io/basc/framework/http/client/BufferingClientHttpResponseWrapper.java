package io.basc.framework.http.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.io.IOUtils;
import lombok.NonNull;

public final class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

	private final ClientHttpResponse response;

	private Pipeline<InputStream, IOException> body;

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

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStream() {
		if (this.body == null) {
			this.body = response.getInputStream().map((e) -> IOUtils.copyToByteArray(e))
					.map((e) -> new ByteArrayInputStream(e));
			this.body = this.body.newPipeline();
		}
		return body;
	}

	public void close() {
		this.response.close();
	}
}
