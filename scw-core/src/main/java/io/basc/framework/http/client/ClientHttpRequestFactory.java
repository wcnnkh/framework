package io.basc.framework.http.client;

import io.basc.framework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

public interface ClientHttpRequestFactory {
	ClientHttpRequest createRequest(URI url, HttpMethod httpMethod) throws IOException;
}
