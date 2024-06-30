package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpOutputMessageWrapper;

public class ClientHttpRequestWrapper<W extends ClientHttpRequest> extends HttpOutputMessageWrapper<W>
		implements ClientHttpRequest {

	public ClientHttpRequestWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getRawMethod() {
		return wrappedTarget.getRawMethod();
	}

	@Override
	public HttpMethod getMethod() {
		return wrappedTarget.getMethod();
	}

	@Override
	public URI getURI() {
		return wrappedTarget.getURI();
	}

	@Override
	public ClientHttpResponse execute() throws IOException {
		return wrappedTarget.execute();
	}

}
