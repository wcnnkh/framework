package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import scw.core.Assert;
import scw.core.instance.InstanceUtils;
import scw.http.HttpMethod;
import scw.http.HttpRequestEntity;
import scw.http.HttpResponseEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.http.client.HttpConnection.AbstractHttpConnection;
import scw.http.client.HttpConnection.RedirectManager;
import scw.http.client.HttpConnectionFactory.AbstractHttpConnectionFactory;
import scw.http.client.exception.HttpClientException;
import scw.http.client.exception.HttpClientResourceAccessException;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.InetUtils;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.uri.UriTemplateHandler;

public class DefaultHttpClient extends AbstractHttpConnectionFactory implements HttpClient {
	private static final ClientHttpRequestFactory CLIENT_HTTP_REQUEST_FACTORY = InstanceUtils
			.loadService(ClientHttpRequestFactory.class,
					"scw.http.client.SimpleClientHttpRequestFactory");
	private static final UriTemplateHandler URI_TEMPLATE_HANDLER = InstanceUtils
			.loadService(UriTemplateHandler.class,
					"scw.net.uri.DefaultUriTemplateHandler");
	static final ClientHttpResponseErrorHandler CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	static final HttpClientCookieManager COOKIE_MANAGER;
	static final List<ClientHttpRequestInterceptor> ROOT_INTERCEPTORS = InstanceUtils
			.loadAllService(ClientHttpRequestInterceptor.class);

	static {
		ClientHttpResponseErrorHandler errorHandler = InstanceUtils
				.loadService(ClientHttpResponseErrorHandler.class);
		CLIENT_HTTP_RESPONSE_ERROR_HANDLER = errorHandler == null ? new DefaultClientHttpResponseErrorHandler()
				: errorHandler;

		COOKIE_MANAGER = InstanceUtils
				.loadService(HttpClientCookieManager.class);
	}

	private static Logger logger = LoggerUtils
			.getLogger(DefaultHttpClient.class);
	private HttpClientCookieManager cookieManager = COOKIE_MANAGER;
	private ClientHttpResponseErrorHandler clientHttpResponseErrorHandler = CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	protected final MultiMessageConverter messageConverter = new MultiMessageConverter();
	private final LinkedList<ClientHttpRequestInterceptor> interceptors = new LinkedList<ClientHttpRequestInterceptor>();
	private ClientHttpRequestFactory clientHttpRequestFactory;
	private UriTemplateHandler uriTemplateHandler;

	public DefaultHttpClient() {
		messageConverter.add(InetUtils.getMessageConverter());
	}

	public LinkedList<ClientHttpRequestInterceptor> getInterceptors() {
		return interceptors;
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public ClientHttpRequestFactory getClientHttpRequestFactory() {
		if (clientHttpRequestFactory == null) {
			return CLIENT_HTTP_REQUEST_FACTORY;
		}
		return clientHttpRequestFactory;
	}

	public void setClientHttpRequestFactory(
			ClientHttpRequestFactory clientHttpRequestFactory) {
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

	protected void requestCallback(ClientHttpRequest request,
			ClientHttpRequestCallback requestCallback) throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
	}

	protected <T> T responseExtractor(ClientHttpRequest request,
			ClientHttpResponse response,
			ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws IOException {
		return clientResponseExtractor == null ? null : clientResponseExtractor
				.execute(response);
	}

	public ClientHttpResponseErrorHandler getClientHttpResponseErrorHandler() {
		return clientHttpResponseErrorHandler;
	}

	public void setClientHttpInputMessageErrorHandler(
			ClientHttpResponseErrorHandler clientHttpResponseErrorHandler) {
		Assert.notNull(clientHttpResponseErrorHandler,
				"ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpResponseErrorHandler = clientHttpResponseErrorHandler;
	}

	public HttpClientCookieManager getCookieManager() {
		return cookieManager;
	}

	public void setCookieManager(HttpClientCookieManager cookieManager) {
		Assert.notNull(clientHttpResponseErrorHandler,
				"HttpClientCookieManager must not be null");
		this.cookieManager = cookieManager;
	}

	protected void handleResponse(ClientHttpRequest request,
			ClientHttpResponse response) throws IOException {
		ClientHttpResponseErrorHandler errorHandler = getClientHttpResponseErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(request.getMethod().name() + " request for \""
						+ request.getURI() + "\" resulted in "
						+ response.getRawStatusCode() + " ("
						+ response.getStatusText() + ")"
						+ (hasError ? "; invoking error handler" : ""));
			} catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException, IOException {
		ClientHttpResponse response = null;
		HttpClientCookieManager cookieManager = getCookieManager();
		if (cookieManager != null) {
			cookieManager.accept(request);
		}

		ClientHttpRequestInterceptorChain chain = new ClientHttpRequestInterceptorChain(
				getInterceptors().iterator());
		ClientHttpRequestInterceptorChain chainToUse = new ClientHttpRequestInterceptorChain(
				ROOT_INTERCEPTORS.iterator(), chain);
		response = chainToUse.intercept(request);
		handleResponse(request, response);
		if (cookieManager != null) {
			cookieManager.accept(response);
		}

		T body = responseExtractor(request, response, responseExtractor);
		return new HttpResponseEntity<T>(body, response.getHeaders(),
				response.getStatusCode());
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			Class<T> responseType) throws HttpClientException, IOException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				request.getMethod(), responseType);
		return execute(request, responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			Type responseType) throws HttpClientException, IOException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				request.getMethod(), responseType);
		return execute(request, responseExtractor);
	}

	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		ClientHttpResponse response = null;
		ClientHttpRequest request;
		try {
			request = requestFactory.createRequest(url, method);
			requestCallback(request, requestCallback);
			return execute(request, responseExtractor);
		} catch (IOException ex) {
			throw new HttpClientResourceAccessException("I/O error on "
					+ method.name() + " request for \"" + url + "\": "
					+ ex.getMessage(), ex);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public final <T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return execute(url, method, getClientHttpRequestFactory(),
				requestCallback, responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return execute(requestEntity.getUrl(), requestEntity.getMethod(),
				createRequestBodyCallback(requestEntity), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, Type responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				requestEntity.getMethod(), responseType);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(),
				createRequestBodyCallback(requestEntity), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, Class<T> responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				requestEntity.getMethod(), responseType);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(),
				createRequestBodyCallback(requestEntity), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		return execute(requestEntity, getClientHttpRequestFactory(),
				responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				requestEntity.getMethod(), responseType);
		return execute(requestEntity, getClientHttpRequestFactory(),
				responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(
			HttpRequestEntity<?> requestEntity, Type responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(
				requestEntity.getMethod(), responseType);
		return execute(requestEntity, getClientHttpRequestFactory(),
				responseExtractor);
	}

	protected ClientHttpRequestCallback createRequestBodyCallback(
			final HttpRequestEntity<?> requestEntity) {
		if (requestEntity.getMethod() == HttpMethod.GET
				&& requestEntity != null && requestEntity.hasBody()) {
			logger.warn("Get request cannot set request body [{}]",
					requestEntity.getUrl());
		}

		final boolean needWriteBody = requestEntity != null
				&& requestEntity.hasBody()
				&& requestEntity.getMethod() != HttpMethod.GET;
		if (needWriteBody) {
			if (!getMessageConverter().canWrite(
					requestEntity.getBody(),
					requestEntity == null ? null : requestEntity
							.getContentType())) {
				throw new NotSupportedException("not supported write "
						+ requestEntity);
			}
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest)
					throws IOException {
				if (requestEntity != null) {
					clientRequest.getHeaders().putAll(
							requestEntity.getHeaders());
				}

				if (needWriteBody) {
					getMessageConverter().write(
							requestEntity.getType(),
							requestEntity.getBody(),
							requestEntity == null ? null : requestEntity
									.getContentType(), clientRequest);
				}
			}
		};
	}

	protected <T> ClientHttpResponseExtractor<T> getClientHttpResponseExtractor(
			HttpMethod httpMethod, final Type responseType) {
		if (httpMethod == HttpMethod.HEAD) {
			return null;
		}

		return new ClientHttpResponseExtractor<T>() {
			@SuppressWarnings("unchecked")
			public T execute(ClientHttpResponse response) throws IOException {
				if (HttpStatus.OK.value() != response.getRawStatusCode()) {
					return null;
				}

				if (!getMessageConverter().canRead(responseType,
						response.getContentType())) {
					throw new NotSupportedException(
							"not supported read responseType=" + responseType);
				}

				return (T) getMessageConverter().read(responseType, response);
			}
		};
	}

	public final HttpConnection createConnection() {
		return new DefaultHttpConnection();
	}

	public final HttpConnection createConnection(HttpMethod method, URI url) {
		return new DefaultHttpConnection(method, url);
	}

	private final class RedirectResponseExtractor<T> implements
			ClientHttpResponseExtractor<T> {
		private final ClientHttpResponseExtractor<T> extractor;
		private final RedirectManager redirectManager;

		public RedirectResponseExtractor(RedirectManager redirectManager, 
				ClientHttpResponseExtractor<T> extractor) {
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
		
		public DefaultHttpConnection(HttpMethod method, URI url){
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
			return requestFactory == null ? DefaultHttpClient.this
					.getClientHttpRequestFactory() : requestFactory;
		}

		public <T> HttpResponseEntity<T> execute(
				ClientHttpResponseExtractor<T> responseExtractor)
				throws HttpClientException {
			ClientHttpResponseExtractor<T> responseExtractorToUse = isRedirectEnable() ? new RedirectResponseExtractor<T>(getRedirectManager(),
					responseExtractor) : responseExtractor;
			HttpResponseEntity<T> responseEntity = DefaultHttpClient.this
					.execute(buildRequestEntity(), getRequestFactory(),
							responseExtractorToUse);
			if (isRedirectEnable()) {
				URI location = getRedirectManager().getRedirect(responseEntity);
				if (location != null) {
					return createConnection(getMethod(), location).execute(responseExtractor);
				}
			}
			return responseEntity;
		}

		public <T> HttpResponseEntity<T> execute(Class<T> responseType)
				throws HttpClientException {
			ClientHttpResponseExtractor<T> clientHttpResponseExtractor = getClientHttpResponseExtractor(
					getMethod(), responseType);
			return execute(clientHttpResponseExtractor);
		}

		public <T> HttpResponseEntity<T> execute(Type responseType)
				throws HttpClientException {
			ClientHttpResponseExtractor<T> clientHttpResponseExtractor = getClientHttpResponseExtractor(
					getMethod(), responseType);
			return execute(clientHttpResponseExtractor);
		}

		@Override
		public AbstractHttpConnection clone() {
			return new DefaultHttpConnection(this);
		}
	}

	public <T> HttpResponseEntity<T> get(Class<T> responseType, String url)
			throws HttpClientException {
		return createConnection(HttpMethod.GET, url).execute(responseType);
	}

	public HttpResponseEntity<Object> get(Type responseType, String url)
			throws HttpClientException {
		return createConnection(HttpMethod.GET, url).execute(responseType);
	}

	public <T> HttpResponseEntity<T> post(Class<T> responseType, String url,
			Object body, MediaType contentType) throws HttpClientException {
		return createConnection(HttpMethod.POST, url).contentType(contentType).body(body).execute(responseType);
	}

	public HttpResponseEntity<Object> post(Type responseType, String url,
			Object body, MediaType contentType) throws HttpClientException {
		return createConnection(HttpMethod.POST, url).contentType(contentType).body(body).execute(responseType);
	}
}
