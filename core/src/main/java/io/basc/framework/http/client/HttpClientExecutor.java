package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.exception.HttpClientException;

public interface HttpClientExecutor {
	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException, IOException;
}
