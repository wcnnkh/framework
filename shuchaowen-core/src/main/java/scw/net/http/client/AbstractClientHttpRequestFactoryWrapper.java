package scw.net.http.client;

import java.io.IOException;
import java.net.URI;

import scw.core.Assert;
import scw.net.http.Method;

public abstract class AbstractClientHttpRequestFactoryWrapper implements ClientHttpRequestFactory {

	private final ClientHttpRequestFactory requestFactory;

	protected AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory) {
		Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
		this.requestFactory = requestFactory;
	}

	public final ClientHttpRequest createRequest(URI uri, Method httpMethod) throws IOException {
		return createRequest(uri, httpMethod, this.requestFactory);
	}

	protected abstract ClientHttpRequest createRequest(URI uri, Method httpMethod,
			ClientHttpRequestFactory requestFactory) throws IOException;

}
