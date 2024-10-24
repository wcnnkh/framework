package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.http.HttpMethod;

public interface ClientHttpRequestFactory {
	default ClientHttpRequest createRequest(URI url, HttpMethod httpMethod) throws IOException {
		return createRequest(url, httpMethod.name());
	}

	ClientHttpRequest createRequest(URI url, String httpMethod) throws IOException;
}
