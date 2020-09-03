package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.http.ContentDisposition;
import scw.http.HttpEntity;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.http.client.accessor.HttpClientConfigAccessor;
import scw.http.client.exception.HttpClientException;
import scw.http.client.exception.HttpClientResourceAccessException;
import scw.io.FileUtils;
import scw.io.support.TemporaryFile;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.InetUtils;
import scw.net.message.converter.MultiMessageConverter;

public abstract class AbstractHttpClient extends HttpClientConfigAccessor implements HttpClient {
	static final ClientHttpResponseErrorHandler CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	static final HttpClientCookieManager COOKIE_MANAGER;

	static {
		ClientHttpResponseErrorHandler errorHandler = InstanceUtils.loadService(ClientHttpResponseErrorHandler.class);
		CLIENT_HTTP_RESPONSE_ERROR_HANDLER = errorHandler == null ? new DefaultClientHttpResponseErrorHandler()
				: errorHandler;

		COOKIE_MANAGER = InstanceUtils.loadService(HttpClientCookieManager.class);
	}
	
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private HttpClientCookieManager cookieManager = COOKIE_MANAGER;
	private ClientHttpResponseErrorHandler clientHttpResponseErrorHandler = CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	protected final MultiMessageConverter messageConverter = new MultiMessageConverter();
	
	public AbstractHttpClient() {
		messageConverter.add(InetUtils.getMessageConverter());
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	protected void requestCallback(ClientHttpRequestBuilder builder, ClientHttpRequest request,
			ClientHttpRequestCallback requestCallback) throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
	}

	protected <T> T responseExtractor(ClientHttpRequestBuilder builder, ClientHttpResponse response,
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

	public HttpClientCookieManager getCookieManager() {
		return cookieManager;
	}

	public void setCookieManager(HttpClientCookieManager cookieManager) {
		Assert.notNull(clientHttpResponseErrorHandler, "HttpClientCookieManager must not be null");
		this.cookieManager = cookieManager;
	}

	protected RuntimeException throwIOException(IOException ex, ClientHttpRequestBuilder builder) {
		return new HttpClientResourceAccessException("I/O error on " + builder.getMethod().name() + " request for \""
				+ builder.getUri() + "\": " + ex.getMessage(), ex);
	}

	protected void handleResponse(ClientHttpRequestBuilder builder, ClientHttpResponse response) throws IOException {
		ClientHttpResponseErrorHandler errorHandler = getClientHttpResponseErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(builder.getMethod().name() + " request for \"" + builder.getUri() + "\" resulted in "
						+ response.getRawStatusCode() + " (" + response.getStatusText() + ")"
						+ (hasError ? "; invoking error handler" : ""));
			} catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	public final <T> HttpResponseEntity<T> execute(ClientHttpRequestBuilder builder,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		ClientHttpResponse response = null;
		ClientHttpRequest request;
		HttpClientCookieManager cookieManager = getCookieManager();
		try {
			request = builder.builder();
			requestCallback(builder, request, requestCallback);
			if (cookieManager != null) {
				cookieManager.accept(request);
			}
			response = request.execute();
			handleResponse(builder, response);
			if (cookieManager != null) {
				cookieManager.accept(response);
			}
			T body = responseExtractor(builder, response, clientResponseExtractor);
			return new HttpResponseEntity<T>(body, response.getHeaders(), response.getStatusCode());
		} catch (IOException ex) {
			throw throwIOException(ex, builder);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	public final <T> HttpResponseEntity<T> execute(URI uri, HttpMethod method, SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		ClientHttpRequestBuilder requestBuilder = createBuilder(uri, method, sslSocketFactory);
		return execute(requestBuilder, createRequestBodyCallback(requestBuilder.getUri(), method, httpEntity),
				clientResponseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(String url, HttpMethod method, SSLSocketFactory sslSocketFactory,
			HttpEntity<?> httpEntity, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		return execute(InetUtils.toURI(url), method, sslSocketFactory, httpEntity, clientResponseExtractor);
	}
	
	public HttpResponseEntity<TemporaryFile> download(String uri, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory, boolean supportedRedirect) throws HttpClientException {
		return download(InetUtils.toURI(uri), httpHeaders, sslSocketFactory, supportedRedirect);
	}
	
	public final HttpResponseEntity<TemporaryFile> download(final URI uri, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory, boolean supportedRedirect) throws HttpClientException {
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(httpHeaders);
		HttpResponseEntity<TemporaryFile> httpResponseEntity = execute(uri, HttpMethod.GET, sslSocketFactory, httpEntity,
				new ClientHttpResponseExtractor<TemporaryFile>() {
					public TemporaryFile execute(ClientHttpResponse response) throws IOException {
						if (response.getStatusCode() != HttpStatus.OK) {
							logger.error("Unable to download:{}, status:{}, statusText:{}", uri,
									response.getRawStatusCode(), response.getStatusText());
							return null;
						}
						
						ContentDisposition contentDisposition = response.getHeaders().getContentDisposition();
						String fileName = contentDisposition == null? null:contentDisposition.getFilename();
						if(StringUtils.isEmpty(fileName)){
							fileName = InetUtils.getFilename(uri.getPath());
						}
						
						TemporaryFile file = new TemporaryFile(fileName);
						if(logger.isDebugEnabled()){
							logger.debug("{} download to {}", uri, file.getPath());
						}
						FileUtils.copyInputStreamToFile(response.getBody(), file);
						return file;
					}
				});
		if(supportedRedirect){
			// 重定向
			if (httpResponseEntity.getStatusCodeValue() == HttpStatus.MOVED_PERMANENTLY.value() || httpResponseEntity.getStatusCodeValue() == HttpStatus.FOUND.value()) {
				URI location = httpResponseEntity.getHeaders().getLocation();
				if (location != null) {
					logger.info("download redirect {} ==> {}", uri, location);
					return download(location, httpHeaders, sslSocketFactory, supportedRedirect);
				}
			}
		}
		return httpResponseEntity;
	}

	protected abstract ClientHttpRequestBuilder createBuilder(URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory);

	protected ClientHttpRequestCallback createRequestBodyCallback(URI uri, HttpMethod httpMethod, final HttpEntity<?> httpEntity) {
		if (httpMethod == HttpMethod.GET && httpEntity != null && httpEntity.hasBody()) {
			logger.warn("Get request cannot set request body [{}]", uri);
		}

		final boolean needWriteBody = httpEntity != null && httpEntity.hasBody() && httpMethod != HttpMethod.GET;
		if (needWriteBody) {
			if (!getMessageConverter().canWrite(httpEntity.getBody(), httpEntity == null ? null : httpEntity.getContentType())) {
				throw new NotSupportedException("not supported write " + httpEntity);
			}
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				clientRequest.getHeaders().set(HttpHeaders.USER_AGENT, getUserAgent());
				if (httpEntity != null) {
					clientRequest.getHeaders().putAll(httpEntity.getHeaders());
				}

				if (needWriteBody) {
					getMessageConverter().write(httpEntity.getBody(), httpEntity == null ? null : httpEntity.getContentType(),
							clientRequest);
				}
			}
		};
	}

	protected <T> ClientHttpResponseExtractor<T> getClientHttpResponseExtractor(HttpMethod httpMethod,
			final Type responseType) {
		if (httpMethod == HttpMethod.HEAD) {
			return null;
		}

		return new ClientHttpResponseExtractor<T>() {
			@SuppressWarnings("unchecked")
			public T execute(ClientHttpResponse response) throws IOException {
				if (HttpStatus.OK.value() != response.getRawStatusCode()) {
					return null;
				}

				if (!getMessageConverter().canRead(responseType, response.getContentType())) {
					throw new NotSupportedException("not supported read responseType=" + responseType);
				}

				return (T) getMessageConverter().read(responseType, response);
			}
		};
	}

	public final <T> HttpResponseEntity<T> execute(Class<? extends T> responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException {
		return execute(responseType, InetUtils.toURI(url), method, sslSocketFactory, httpEntity);
	}

	@SuppressWarnings("unchecked")
	public final <T> HttpResponseEntity<T> execute(Class<? extends T> responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException {
		return (HttpResponseEntity<T>) execute(uri, method, sslSocketFactory, httpEntity,
				getClientHttpResponseExtractor(method, responseType));
	}

	public final HttpResponseEntity<Object> execute(Type responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException {
		return execute(responseType, InetUtils.toURI(url), method, sslSocketFactory, httpEntity);
	}

	public final HttpResponseEntity<Object> execute(Type responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException {
		return execute(uri, method, sslSocketFactory, httpEntity,
				getClientHttpResponseExtractor(method, responseType));
	}

	public final <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public final <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null);
	}

	public final HttpResponseEntity<Object> get(Type responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public final HttpResponseEntity<Object> get(Type responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null);
	}

	public final <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url, Object body,
			MediaType contentType) throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public final <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException {
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(body);
		httpEntity.getHeaders().setContentType(contentType);
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, httpEntity);
	}

	public final HttpResponseEntity<Object> post(Type responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public final HttpResponseEntity<Object> post(Type responseType, String url, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException {
		HttpEntity<Object> httpEntity = new HttpEntity<Object>(body);
		httpEntity.getHeaders().setContentType(contentType);
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, httpEntity);
	}
}
