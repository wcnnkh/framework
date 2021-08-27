package io.basc.framework.http.client;

import java.io.IOException;

@FunctionalInterface
public interface ClientHttpResponseExtractor<T> {
	T execute(ClientHttpResponse response) throws IOException;
}
