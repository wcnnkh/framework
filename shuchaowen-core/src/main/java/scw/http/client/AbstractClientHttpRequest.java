package scw.http.client;

import java.io.IOException;
import java.io.OutputStream;

import scw.core.Assert;
import scw.http.AbstractHttpOutputMessage;
import scw.http.HttpHeaders;
import scw.http.MediaType;

public abstract class AbstractClientHttpRequest extends AbstractHttpOutputMessage implements ClientHttpRequest {
	private final HttpHeaders headers = new HttpHeaders();

	private boolean executed = false;

	public final HttpHeaders getHeaders() {
		if (executed) {
			headers.readyOnly();
		}
		return headers;
	}

	public final OutputStream getBody() throws IOException {
		assertNotExecuted();
		return getBodyInternal(this.headers);
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
