package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;

import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.lang.Nullable;

public interface HttpClientExecutor<H extends HttpClientExecutor<H>> extends HttpClientConfigurable<H> {

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(request, getCookieHandler(), responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, @Nullable CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException;

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(uri, httpMethod, getRequestFactory(), getCookieHandler(), requestCallback, getRedirectManager(),
				responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			@Nullable CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback,
			@Nullable RedirectManager redirectManager, ClientHttpResponseExtractor<T> responseExtractor);
}
