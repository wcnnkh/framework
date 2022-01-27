package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.http.client.exception.HttpClientResourceAccessException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.net.uri.UriTemplateHandler;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;

public class DefaultHttpClient extends AbstractHttpConnectionFactory implements HttpClient, Configurable {
	/**
	 * 默认的client http request factory
	 */
	public static final ClientHttpRequestFactory CLIENT_HTTP_REQUEST_FACTORY = Sys.env
			.getServiceLoader(ClientHttpRequestFactory.class).first();
	private static final UriTemplateHandler URI_TEMPLATE_HANDLER = Sys.env
			.getServiceLoader(UriTemplateHandler.class, "io.basc.framework.net.uri.DefaultUriTemplateHandler").first();
	static final ClientHttpResponseErrorHandler CLIENT_HTTP_RESPONSE_ERROR_HANDLER = Sys.env
			.getServiceLoader(ClientHttpResponseErrorHandler.class, DefaultClientHttpResponseErrorHandler.class)
			.first();
	static final CookieHandler COOKIE_HANDLER = Sys.env.getServiceLoader(CookieHandler.class).first();
	static final List<ClientHttpRequestInterceptor> ROOT_INTERCEPTORS = Sys.env
			.getServiceLoader(ClientHttpRequestInterceptor.class).toList();

	private static Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
	private CookieHandler cookieHandler = COOKIE_HANDLER;
	private ClientHttpResponseErrorHandler clientHttpResponseErrorHandler = CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	protected final MessageConverters messageConverters = new DefaultMessageConverters();
	private final ConfigurableServices<ClientHttpRequestInterceptor> interceptors = new ConfigurableServices<>(
			ClientHttpRequestInterceptor.class);
	private ClientHttpRequestFactory clientHttpRequestFactory;
	private UriTemplateHandler uriTemplateHandler;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		interceptors.configure(serviceLoaderFactory);
		messageConverters.configure(serviceLoaderFactory);

		if (serviceLoaderFactory.isInstance(UriTemplateHandler.class)) {
			setUriTemplateHandler(serviceLoaderFactory.getInstance(UriTemplateHandler.class));
		}

		if (serviceLoaderFactory.isInstance(CookieHandler.class)) {
			setCookieHandler(serviceLoaderFactory.getInstance(CookieHandler.class));
		}

		if (serviceLoaderFactory.isInstance(ClientHttpResponseErrorHandler.class)) {
			setClientHttpResponseErrorHandler(serviceLoaderFactory.getInstance(ClientHttpResponseErrorHandler.class));
		}

		if (serviceLoaderFactory.isInstance(ClientHttpRequestFactory.class)) {
			setClientHttpRequestFactory(serviceLoaderFactory.getInstance(ClientHttpRequestFactory.class));
		}
	}

	public ConfigurableServices<ClientHttpRequestInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setClientHttpResponseErrorHandler(ClientHttpResponseErrorHandler clientHttpResponseErrorHandler) {
		this.clientHttpResponseErrorHandler = clientHttpResponseErrorHandler;
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	public ClientHttpRequestFactory getClientHttpRequestFactory() {
		if (clientHttpRequestFactory == null) {
			return CLIENT_HTTP_REQUEST_FACTORY;
		}
		return clientHttpRequestFactory;
	}

	public void setClientHttpRequestFactory(ClientHttpRequestFactory clientHttpRequestFactory) {
		this.clientHttpRequestFactory = clientHttpRequestFactory;
	}

	public UriTemplateHandler getUriTemplateHandler() {
		if (uriTemplateHandler == null) {
			return URI_TEMPLATE_HANDLER;
		}
		return uriTemplateHandler;
	}

	public void setUriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
		this.uriTemplateHandler = uriTemplateHandler;
	}

	protected void requestCallback(ClientHttpRequest request, ClientHttpRequestCallback requestCallback)
			throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
	}

	protected <T> T responseExtractor(ClientHttpRequest request, ClientHttpResponse response,
			ClientHttpResponseExtractor<T> clientResponseExtractor) throws IOException {
		return clientResponseExtractor == null ? null : clientResponseExtractor.execute(response);
	}

	public ClientHttpResponseErrorHandler getClientHttpResponseErrorHandler() {
		return clientHttpResponseErrorHandler;
	}

	public void setClientHttpInputMessageErrorHandler(ClientHttpResponseErrorHandler clientHttpResponseErrorHandler) {
		Assert.notNull(clientHttpResponseErrorHandler, "ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpResponseErrorHandler = clientHttpResponseErrorHandler;
	}

	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	public void setCookieHandler(CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

	protected void handleResponse(ClientHttpRequest request, ClientHttpResponse response) throws IOException {
		ClientHttpResponseErrorHandler errorHandler = getClientHttpResponseErrorHandler();
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

	protected <T> HttpResponseEntity<T> execute(CookieHandler cookieHandler, ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		ClientHttpResponse response = null;
		if (cookieHandler != null) {
			Map<String, List<String>> map = cookieHandler.get(request.getURI(), request.getHeaders());
			if (!CollectionUtils.isEmpty(map)) {
				request.getHeaders().addAll(map);
			}
		}

		ClientHttpRequestInterceptorChain chain = new ClientHttpRequestInterceptorChain(getInterceptors().iterator());
		ClientHttpRequestInterceptorChain chainToUse = new ClientHttpRequestInterceptorChain(
				ROOT_INTERCEPTORS.iterator(), chain);
		response = chainToUse.intercept(request);
		handleResponse(request, response);
		if (cookieHandler != null) {
			cookieHandler.put(request.getURI(), response.getHeaders());
		}

		T body = responseExtractor(request, response, responseExtractor);
		return new HttpResponseEntity<T>(body, response.getHeaders(), response.getStatusCode());
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(getCookieHandler(), request, responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, Class<T> responseType)
			throws HttpClientException, IOException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(request.getMethod(),
				TypeDescriptor.valueOf(responseType));
		return execute(request, responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, TypeDescriptor responseType)
			throws HttpClientException, IOException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(request.getMethod(),
				responseType);
		return execute(request, responseExtractor);
	}

	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		ClientHttpResponse response = null;
		ClientHttpRequest request;
		try {
			request = requestFactory.createRequest(url, method);
			requestCallback(request, requestCallback);
			return execute(request, responseExtractor);
		} catch (IOException ex) {
			throw new HttpClientResourceAccessException(
					"I/O error on " + method.name() + " request for \"" + url + "\": " + ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public final <T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return execute(url, method, getClientHttpRequestFactory(), requestCallback, responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return execute(requestEntity.getURI(), requestEntity.getMethod(), createRequestBodyCallback(requestEntity),
				responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, TypeDescriptor responseType) throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				responseType);
		return execute(requestEntity.getURI(), requestEntity.getMethod(), createRequestBodyCallback(requestEntity),
				responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, Class<T> responseType) throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				TypeDescriptor.valueOf(responseType));
		return execute(requestEntity.getURI(), requestEntity.getMethod(), createRequestBodyCallback(requestEntity),
				responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException {
		return execute(requestEntity, getClientHttpRequestFactory(), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				TypeDescriptor.valueOf(responseType));
		return execute(requestEntity, getClientHttpRequestFactory(), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, TypeDescriptor responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				responseType);
		return execute(requestEntity, getClientHttpRequestFactory(), responseExtractor);
	}

	protected ClientHttpRequestCallback createRequestBodyCallback(final HttpRequestEntity<?> requestEntity) {
		if (requestEntity.getMethod() == HttpMethod.GET && requestEntity != null && requestEntity.hasBody()) {
			logger.warn("Get request cannot set request body [{}]", requestEntity.getURI());
		}

		final boolean needWriteBody = requestEntity != null && requestEntity.hasBody()
				&& requestEntity.getMethod() != HttpMethod.GET;
		if (needWriteBody) {
			if (!getMessageConverters().canWrite(requestEntity.getTypeDescriptor(), requestEntity.getBody(),
					requestEntity == null ? null : requestEntity.getContentType())) {
				throw new NotSupportedException("not supported write " + requestEntity);
			}
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				if (requestEntity != null) {
					clientRequest.getHeaders().putAll(requestEntity.getHeaders());
				}

				if (needWriteBody) {
					getMessageConverters().write(requestEntity.getTypeDescriptor(), requestEntity.getBody(),
							requestEntity == null ? null : requestEntity.getContentType(), clientRequest);
				}
			}
		};
	}

	protected <T> ClientHttpResponseExtractor<T> getClientHttpResponseExtractor(HttpMethod httpMethod,
			final TypeDescriptor responseType) {
		if (httpMethod == HttpMethod.HEAD) {
			return null;
		}

		return new ClientHttpResponseExtractor<T>() {
			@SuppressWarnings("unchecked")
			public T execute(ClientHttpResponse response) throws IOException {
				if (HttpStatus.OK.value() != response.getRawStatusCode()) {
					return null;
				}

				if (!getMessageConverters().canRead(responseType, response.getContentType())) {
					throw new NotSupportedException("not supported read responseType=" + responseType);
				}

				return (T) getMessageConverters().read(responseType, response);
			}
		};
	}

	public final HttpConnection createConnection() {
		return new DefaultHttpConnection();
	}

	public final HttpConnection createConnection(HttpMethod method, URI url) {
		return new DefaultHttpConnection(method, url);
	}

	private final class RedirectResponseExtractor<T> implements ClientHttpResponseExtractor<T> {
		private final ClientHttpResponseExtractor<T> extractor;
		private final RedirectManager redirectManager;

		public RedirectResponseExtractor(RedirectManager redirectManager, ClientHttpResponseExtractor<T> extractor) {
			this.redirectManager = redirectManager;
			this.extractor = extractor;
		}

		public T execute(ClientHttpResponse response) throws IOException {
			URI url = redirectManager.getRedirect(response);
			if (url == null) {
				return extractor == null ? null : extractor.execute(response);
			}
			return null;
		}
	}

	private final class DefaultHttpConnection extends AbstractHttpConnection {

		public DefaultHttpConnection() {
		}

		public DefaultHttpConnection(HttpMethod method, URI url) {
			super(method, url);
		}

		public DefaultHttpConnection(DefaultHttpConnection httpConnection) {
			super(httpConnection);
		}

		@Override
		protected UriTemplateHandler getUriTemplateHandler() {
			return DefaultHttpClient.this.getUriTemplateHandler();
		}

		@Override
		public ClientHttpRequestFactory getRequestFactory() {
			ClientHttpRequestFactory requestFactory = super.getRequestFactory();
			return requestFactory == null ? DefaultHttpClient.this.getClientHttpRequestFactory() : requestFactory;
		}

		public <T> HttpResponseEntity<T> execute(ClientHttpResponseExtractor<T> responseExtractor)
				throws HttpClientException {
			ClientHttpResponseExtractor<T> responseExtractorToUse = isRedirectEnable()
					? new RedirectResponseExtractor<T>(getRedirectManager(), responseExtractor)
					: responseExtractor;
			HttpResponseEntity<T> responseEntity = DefaultHttpClient.this.execute(buildRequestEntity(),
					getRequestFactory(), responseExtractorToUse);
			if (isRedirectEnable()) {
				URI location = getRedirectManager().getRedirect(responseEntity);
				if (location != null) {
					return createConnection(getMethod(), location).execute(responseExtractor);
				}
			}
			return responseEntity;
		}

		public <T> HttpResponseEntity<T> execute(Class<T> responseType) throws HttpClientException {
			ClientHttpResponseExtractor<T> clientHttpResponseExtractor = getClientHttpResponseExtractor(getMethod(),
					TypeDescriptor.valueOf(responseType));
			return execute(clientHttpResponseExtractor);
		}

		public <T> HttpResponseEntity<T> execute(TypeDescriptor responseType) throws HttpClientException {
			ClientHttpResponseExtractor<T> clientHttpResponseExtractor = getClientHttpResponseExtractor(getMethod(),
					responseType);
			return execute(clientHttpResponseExtractor);
		}

		@Override
		public AbstractHttpConnection clone() {
			return new DefaultHttpConnection(this);
		}
	}

	public <T> HttpResponseEntity<T> get(Class<T> responseType, String url) throws HttpClientException {
		return createConnection(HttpMethod.GET, url).execute(responseType);
	}

	public <T> HttpResponseEntity<T> get(TypeDescriptor responseType, String url) throws HttpClientException {
		return createConnection(HttpMethod.GET, url).execute(responseType);
	}

	public <T> HttpResponseEntity<T> post(Class<T> responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return createConnection(HttpMethod.POST, url).contentType(contentType).body(body).execute(responseType);
	}

	public <T> HttpResponseEntity<T> post(TypeDescriptor responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return createConnection(HttpMethod.POST, url).contentType(contentType).body(body).execute(responseType);
	}
}
