package io.basc.framework.http.client;

import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.MediaType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.function.Pipeline;
import lombok.NonNull;

public abstract class AbstractClientHttpRequest implements ClientHttpRequest {
	private final HttpHeaders headers = new HttpHeaders();

	private boolean executed = false;

	public final HttpHeaders getHeaders() {
		if (executed) {
			headers.readyOnly();
		}
		return headers;
	}

	@Override
	public @NonNull Pipeline<OutputStream, IOException> getOutputStream() {
		assertNotExecuted();
		return Pipeline.of(() -> getBodyInternal(headers)).newPipeline();
	}

	@Override
	public MediaType getContentType() {
		return getHeaders().getContentType();
	}

	public final ClientHttpResponse execute() throws IOException {
		assertNotExecuted();
		ClientHttpResponse result = executeInternal(this.headers);
		this.executed = true;
		return result;
	}

	protected void assertNotExecuted() {
		Assert.state(!this.executed, "ClientHttpRequest already executed");
	}

	protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException;

}
