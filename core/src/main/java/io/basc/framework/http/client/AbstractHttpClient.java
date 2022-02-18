package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.net.uri.DefaultUriTemplateHandler;
import io.basc.framework.net.uri.UriTemplateHandler;
import io.basc.framework.util.CollectionUtils;

public abstract class AbstractHttpClient implements HttpClient, Configurable {
	private ClientHttpRequestFactory requestFactory;
	private CookieHandler cookieHandler;
	private RedirectManager redirectManager;
	private boolean cloneBeforeSet;
	private UriTemplateHandler uriTemplateHandler;
	private ClientHttpResponseErrorHandler responseErrorHandler;
	private ClientHttpRequestInterceptor interceptor;
	private MessageConverter messageConverter;

	public AbstractHttpClient(ClientHttpRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public AbstractHttpClient(AbstractHttpClient configurable) {
		this.requestFactory = configurable.requestFactory;
		this.cookieHandler = configurable.cookieHandler;
		this.redirectManager = configurable.redirectManager;
		this.cloneBeforeSet = configurable.cloneBeforeSet;
		this.uriTemplateHandler = configurable.uriTemplateHandler;
		this.responseErrorHandler = configurable.responseErrorHandler;
		this.interceptor = configurable.interceptor;
		this.messageConverter = configurable.messageConverter;
	}

	public boolean isCloneBeforeSet() {
		return cloneBeforeSet;
	}

	public void setCloneBeforeSet(boolean cloneBeforeSet) {
		this.cloneBeforeSet = cloneBeforeSet;
	}

	public abstract AbstractHttpClient clone();

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory.isInstance(ClientHttpRequestFactory.class)) {
			this.requestFactory = serviceLoaderFactory.getInstance(ClientHttpRequestFactory.class);
		}

		if (serviceLoaderFactory.isInstance(CookieHandler.class)) {
			this.cookieHandler = serviceLoaderFactory.getInstance(CookieHandler.class);
		}

		if (serviceLoaderFactory.isInstance(RedirectManager.class)) {
			this.redirectManager = serviceLoaderFactory.getInstance(RedirectManager.class);
		}

		if (serviceLoaderFactory.isInstance(UriTemplateHandler.class)) {
			this.uriTemplateHandler = serviceLoaderFactory.getInstance(UriTemplateHandler.class);
		}

		if (serviceLoaderFactory.isInstance(ClientHttpResponseErrorHandler.class)) {
			this.responseErrorHandler = serviceLoaderFactory.getInstance(ClientHttpResponseErrorHandler.class);
		}
	}

	public UriTemplateHandler getUriTemplateHandler() {
		return uriTemplateHandler == null ? DefaultUriTemplateHandler.INSTANCE : uriTemplateHandler;
	}

	public void setUriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
		this.uriTemplateHandler = uriTemplateHandler;
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

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public ClientHttpRequestFactory getRequestFactory() {
		return requestFactory;
	}

	@Override
	public AbstractHttpClient setRequestFactory(ClientHttpRequestFactory requestFactory) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.requestFactory = requestFactory;
		return client;
	}

	@Override
	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	@Override
	public AbstractHttpClient setCookieHandler(CookieHandler cookieHandler) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.cookieHandler = cookieHandler;
		return client;
	}

	@Override
	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	@Override
	public AbstractHttpClient setRedirectManager(RedirectManager redirectManager) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.redirectManager = redirectManager;
		return client;
	}

	@Override
	public HttpConnection createConnection(String httpMethod, URI uri) {
		HttpClientConnection connection = new HttpClientConnection(uri, httpMethod, this);
		connection.setCloneBeforeSet(true);
		return connection;
	}

	@Override
	public final HttpConnection createConnection(String httpMethod, String uriTemplate, Map<String, ?> uriVariables) {
		URI uri = getUriTemplateHandler().expand(uriTemplate, uriVariables);
		return createConnection(httpMethod, uri);
	}

	@Override
	public final HttpConnection createConnection(String httpMethod, String uriTemplate, Object... uriVariables) {
		URI uri = getUriTemplateHandler().expand(uriTemplate, uriVariables);
		return createConnection(httpMethod, uri);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(request, cookieHandler, responseExtractor, null);
	}

	protected <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor, @Nullable TypeDescriptor responseType)
			throws HttpClientException, IOException {
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
		return new HttpResponseEntity<T>(body, responseType, response.getHeaders(), response.getStatusCode());
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
		return clientResponseExtractor == null ? null : clientResponseExtractor.execute(request, response);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, null);
	}

	protected <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, responseType, 0);
	}

	protected <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType, long deep) {
		HttpResponseEntity<T> responseEntity;
		ClientHttpRequest request;
		try {
			request = requestFactory.createRequest(uri, httpMethod);
			request = requestCallback(request, requestCallback);
			responseEntity = execute(request, cookieHandler, responseExtractor, responseType);
		} catch (IOException ex) {
			throw new HttpClientResourceAccessException(
					"I/O error on " + httpMethod + " request for \"" + uri + "\": " + ex.getMessage(), ex);
		}

		if (redirectManager == null) {
			return responseEntity;
		}

		URI redirectUri = redirectManager.getRedirect(request, responseEntity, deep);
		if (redirectUri == null) {
			return responseEntity;
		}

		return execute(redirectUri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, responseType, deep + 1);
	}

	protected ClientHttpRequest requestCallback(ClientHttpRequest request, ClientHttpRequestCallback requestCallback)
			throws IOException {
		if (requestCallback != null) {
			return requestCallback.callback(request);
		}
		return request;
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			TypeDescriptor responseType) throws HttpClientException, IOException {
		return execute(request, cookieHandler,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(requestEntity.getURI(), requestEntity.getRawMethod(), requestFactory, cookieHandler,
				new MessageConverterClientHttpRequestCallback(getMessageConverter(), requestEntity), redirectManager,
				responseExtractor);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler, RedirectManager redirectManager,
			TypeDescriptor responseType) {
		return execute(requestEntity.getURI(), requestEntity.getRawMethod(), requestFactory, cookieHandler,
				new MessageConverterClientHttpRequestCallback(getMessageConverter(), requestEntity), redirectManager,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			TypeDescriptor responseType) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}
}
