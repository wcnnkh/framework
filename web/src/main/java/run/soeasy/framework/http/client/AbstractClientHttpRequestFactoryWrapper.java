package run.soeasy.framework.http.client;

import java.io.IOException;
import java.net.URI;

import run.soeasy.framework.http.HttpMethod;
import run.soeasy.framework.util.Assert;

public abstract class AbstractClientHttpRequestFactoryWrapper implements ClientHttpRequestFactory {

	private final ClientHttpRequestFactory requestFactory;

	protected AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory) {
		Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
		this.requestFactory = requestFactory;
	}

	public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return createRequest(uri, httpMethod, this.requestFactory);
	}

	protected abstract ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod,
			ClientHttpRequestFactory requestFactory) throws IOException;

}
