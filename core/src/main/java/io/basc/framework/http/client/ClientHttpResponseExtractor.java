package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpRequest;

@FunctionalInterface
public interface ClientHttpResponseExtractor<T> {
	T execute(HttpRequest request, ClientHttpResponse response) throws IOException;
}
