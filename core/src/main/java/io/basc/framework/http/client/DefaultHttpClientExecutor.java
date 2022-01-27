package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.basc.framework.env.Sys;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.uri.UriTemplateHandler;
import io.basc.framework.util.CollectionUtils;

public class DefaultHttpClientExecutor implements HttpClientExecutor, Configurable {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpClientExecutor.class);
	private static final ClientHttpResponseErrorHandler CLIENT_HTTP_RESPONSE_ERROR_HANDLER = Sys.env
			.getServiceLoader(ClientHttpResponseErrorHandler.class, DefaultClientHttpResponseErrorHandler.class)
			.first();

	/**
	 * 默认的client http request factory
	 */
	public static final ClientHttpRequestFactory CLIENT_HTTP_REQUEST_FACTORY = Sys.env
			.getServiceLoader(ClientHttpRequestFactory.class).first();
	private static final CookieHandler COOKIE_HANDLER = Sys.env.getServiceLoader(CookieHandler.class).first();
	private ClientHttpRequestFactory requestFactory;
	private RedirectManager redirectManager;
	private UriTemplateHandler uriTemplateHandler;
	private CookieHandler cookieHandler;
	private ClientHttpResponseErrorHandler responseErrorHandler;
	private ClientHttpRequestInterceptor interceptor;

	public DefaultHttpClientExecutor() {
	}

	public DefaultHttpClientExecutor(DefaultHttpClientExecutor executor) {
		this.requestFactory = executor.requestFactory;
		this.redirectManager = executor.redirectManager;
		this.uriTemplateHandler = executor.uriTemplateHandler;
		this.cookieHandler = executor.cookieHandler;
		this.responseErrorHandler = executor.responseErrorHandler;
		this.interceptor = executor.interceptor;
	}

	public ClientHttpRequestFactory getRequestFactory() {
		return requestFactory == null ? CLIENT_HTTP_REQUEST_FACTORY : requestFactory;
	}

	public DefaultHttpClientExecutor setRequestFactory(ClientHttpRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
		return this;
	}

	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	public DefaultHttpClientExecutor setRedirectManager(RedirectManager redirectManager) {
		this.redirectManager = redirectManager;
		return this;
	}

	public CookieHandler getCookieHandler() {
		return cookieHandler == null ? COOKIE_HANDLER : cookieHandler;
	}

	public DefaultHttpClientExecutor setCookieHandler(CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
		return this;
	}

	public ClientHttpResponseErrorHandler getResponseErrorHandler() {
		return responseErrorHandler == null ? CLIENT_HTTP_RESPONSE_ERROR_HANDLER : responseErrorHandler;
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
		if (logger.isDebugEnabled()) {
			logger.debug(request.getMethod().name() + " request for \"" + request.getURI() + "\" resulted in "
					+ response.getRawStatusCode() + " (" + response.getStatusText() + ")"
					+ (hasError ? "; invoking error handler" : ""));
		}
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
			setCookieHandler(serviceLoaderFactory.getInstance(CookieHandler.class));
		}

		if (serviceLoaderFactory.isInstance(ClientHttpResponseErrorHandler.class)) {
			setResponseErrorHandler(serviceLoaderFactory.getInstance(ClientHttpResponseErrorHandler.class));
		}

		if (serviceLoaderFactory.isInstance(ClientHttpRequestFactory.class)) {
			setRequestFactory(serviceLoaderFactory.getInstance(ClientHttpRequestFactory.class));
		}
	}
}
