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
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.http.client.exception.HttpClientResourceAccessException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.message.convert.MessageConverter;
import io.basc.framework.util.CollectionUtils;

public abstract class AbstractHttpConnectionFactory implements HttpClient, Configurable {
	private static Logger logger = LoggerFactory.getLogger(AbstractHttpConnectionFactory.class);
	private MessageConverter messageConverter;
	private RedirectManager redirectManager;
	private CookieHandler cookieHandler;
	private ClientHttpResponseErrorHandler responseErrorHandler;
	private ClientHttpRequestInterceptor interceptor;

	public AbstractHttpConnectionFactory() {
	}

	public AbstractHttpConnectionFactory(AbstractHttpConnectionFactory connectionFactory) {
		this.messageConverter = connectionFactory.messageConverter;
		this.redirectManager = connectionFactory.redirectManager;
		this.cookieHandler = connectionFactory.cookieHandler;
		this.responseErrorHandler = connectionFactory.responseErrorHandler;
		this.interceptor = connectionFactory.interceptor;
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	public void setRedirectManager(RedirectManager redirectManager) {
		this.redirectManager = redirectManager;
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

	protected void requestCallback(ClientHttpRequest request, ClientHttpRequestCallback requestCallback)
			throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
	}

	@Override
	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException {
		return execute(url, method, getRequestFactory(), requestCallback, responseExtractor, 0);
	}

	protected <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor, long deep)
			throws HttpClientException {
		HttpResponseEntity<T> responseEntity;
		try {
			ClientHttpRequest request = requestFactory.createRequest(url, method);
			requestCallback(request, requestCallback);
			responseEntity = execute(request, responseExtractor);
		} catch (IOException ex) {
			throw new HttpClientResourceAccessException(
					"I/O error on " + method.name() + " request for \"" + url + "\": " + ex.getMessage(), ex);
		}

		RedirectManager redirectManager = getRedirectManager();
		if (redirectManager == null) {
			return responseEntity;
		}

		URI redirectUri = redirectManager.getRedirect(responseEntity, deep);
		if (redirectUri == null) {
			return responseEntity;
		}

		return execute(redirectUri, method, requestFactory, requestCallback, responseExtractor, deep + 1);
	}

	protected ClientHttpRequestCallback createRequestBodyCallback(final HttpRequestEntity<?> requestEntity) {
		if (requestEntity.getMethod() == HttpMethod.GET && requestEntity != null && requestEntity.hasBody()) {
			logger.warn("Get request cannot set request body [{}]", requestEntity.getURI());
		}

		MessageConverter messageConverter = getMessageConverter();
		final boolean needWriteBody = requestEntity != null && requestEntity.hasBody()
				&& requestEntity.getMethod() != HttpMethod.GET;
		if (requestEntity.getMethod().hasRequestBody()) {
			if (messageConverter == null || !messageConverter.canWrite(requestEntity.getTypeDescriptor(),
					requestEntity.getBody(), requestEntity == null ? null : requestEntity.getContentType())) {
				throw new NotSupportedException("not supported write " + requestEntity);
			}
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				if (requestEntity != null) {
					clientRequest.getHeaders().putAll(requestEntity.getHeaders());
				}

				if (needWriteBody) {
					messageConverter.write(requestEntity.getTypeDescriptor(), requestEntity.getBody(),
							requestEntity == null ? null : requestEntity.getContentType(), clientRequest);
				}
			}
		};
	}

	protected <T> ClientHttpResponseExtractor<T> getClientHttpResponseExtractor(HttpMethod httpMethod,
			final TypeDescriptor responseType) {
		if (!httpMethod.hasResponseBody()) {
			return null;
		}

		return new ClientHttpResponseExtractor<T>() {
			@SuppressWarnings("unchecked")
			public T execute(ClientHttpResponse response) throws IOException {
				if (HttpStatus.OK.value() != response.getRawStatusCode()) {
					return null;
				}

				MessageConverter messageConverter = getMessageConverter();
				if (messageConverter == null || !messageConverter.canRead(responseType, response.getContentType())) {
					throw new NotSupportedException("not supported read responseType=" + responseType);
				}

				return (T) messageConverter.read(responseType, response);
			}
		};
	}

	@Override
	public <T> HttpResponseEntity<T> execute(ClientHttpRequest request, TypeDescriptor responseType)
			throws HttpClientException, IOException {
		return execute(request, getClientHttpResponseExtractor(request.getMethod(), responseType));
	}

	@Override
	public <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException {
		return execute(requestEntity.getURI(), requestEntity.getMethod(), createRequestBodyCallback(requestEntity),
				responseExtractor);
	}

	@Override
	public <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, TypeDescriptor responseType)
			throws HttpClientException {
		return execute(requestEntity, getClientHttpResponseExtractor(requestEntity.getMethod(), responseType));
	}

	@Override
	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, TypeDescriptor responseType,
			ClientHttpRequestCallback requestCallback) throws HttpClientException {
		return execute(url, method, requestCallback, getClientHttpResponseExtractor(method, responseType));
	}
}