package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;

import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.exception.HttpClientException;

public interface HttpClientExecutor {
	CookieHandler getCookieHandler();

	RedirectManager getRedirectManager();

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(request, getCookieHandler(), responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException;

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(uri, httpMethod, requestFactory, getCookieHandler(), requestCallback, getRedirectManager(),
				responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor);
}
