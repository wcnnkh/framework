package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.util.CollectionUtils;

public class DefaultHttpClientExecutor implements HttpClientExecutor, Configurable {
	private CookieHandler cookieHandler;
	private ClientHttpResponseErrorHandler responseErrorHandler;
	private ClientHttpRequestInterceptor interceptor;

	public DefaultHttpClientExecutor() {
	}

	public DefaultHttpClientExecutor(DefaultHttpClientExecutor executor) {
		this.cookieHandler = executor.cookieHandler;
		this.responseErrorHandler = executor.responseErrorHandler;
		this.interceptor = executor.interceptor;
	}

	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	public void setCookieHandler(CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

	public ClientHttpResponseErrorHandler getResponseErrorHandler() {
		return responseErrorHandler;
	}

	public void setResponseErrorHandler(ClientHttpResponseErrorHandler responseErrorHandler) {
		this.responseErrorHandler = responseErrorHandler;
	}

	public ClientHttpRequestInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(ClientHttpRequestInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	@Override
	public <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		ClientHttpResponse response = null;
		if (cookieHandler != null) {
			Map<String, List<String>> map = cookieHandler.get(request.getURI(), request.getHeaders());
			if (!CollectionUtils.isEmpty(map)) {
				request.getHeaders().addAll(map);
			}
		}

		ClientHttpRequestInterceptor interceptor = getInterceptor();
		if (interceptor == null) {
			response = request.execute();
		} else {
			ClientHttpRequestInterceptorChain chain = new ClientHttpRequestInterceptorChain(
					Arrays.asList(interceptor).iterator());
			response = chain.execute(request);
		}

		handleResponse(request, response);
		if (cookieHandler != null) {
			cookieHandler.put(request.getURI(), response.getHeaders());
		}

		T body = responseExtractor(request, response, responseExtractor);
		return new HttpResponseEntity<T>(body, response.getHeaders(), response.getStatusCode());
	}

	protected void handleResponse(ClientHttpRequest request, ClientHttpResponse response) throws IOException {
		ClientHttpResponseErrorHandler errorHandler = getResponseErrorHandler();
		if (errorHandler == null) {
			return;
		}

		boolean hasError = errorHandler.hasError(response);
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	protected <T> T responseExtractor(ClientHttpRequest request, ClientHttpResponse response,
			ClientHttpResponseExtractor<T> clientResponseExtractor) throws IOException {
		return clientResponseExtractor == null ? null : clientResponseExtractor.execute(response);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory.isInstance(CookieHandler.class)) {
			this.cookieHandler = serviceLoaderFactory.getInstance(CookieHandler.class);
		}

		if (serviceLoaderFactory.isInstance(ClientHttpResponseErrorHandler.class)) {
			this.responseErrorHandler = serviceLoaderFactory.getInstance(ClientHttpResponseErrorHandler.class);
		}
	}
}
