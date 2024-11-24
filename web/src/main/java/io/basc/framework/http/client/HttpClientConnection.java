package io.basc.framework.http.client;

import java.net.URI;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpResponseEntity;

public class HttpClientConnection extends AbstractHttpConnection {
	private final HttpClient httpClient;

	public HttpClientConnection(URI uri, String httpMethod, HttpClient httpClient) {
		super(uri, httpMethod);
		setRequestFactory(httpClient.getRequestFactory());
		setCookieHandler(httpClient.getCookieHandler());
		setRedirectManager(httpClient.getRedirectManager());
		this.httpClient = httpClient;
	}

	protected HttpClientConnection(HttpClientConnection connection) {
		super(connection);
		this.httpClient = connection.httpClient;
	}

	@Override
	public <T> HttpResponseEntity<T> execute(ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return httpClient.execute(build(), getRequestFactory(), getCookieHandler(), getRedirectManager(),
				responseExtractor);
	}

	@Override
	public <T> HttpResponseEntity<T> execute(TypeDescriptor responseType) throws HttpClientException {
		return httpClient.execute(build(), getRequestFactory(), getCookieHandler(), getRedirectManager(), responseType);
	}

	@Override
	public HttpClientConnection clone() {
		return new HttpClientConnection(this);
	}

}
