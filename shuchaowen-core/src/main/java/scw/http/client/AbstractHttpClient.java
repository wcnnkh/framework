package scw.http.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;
import scw.http.client.exception.HttpClientResourceAccessException;
import scw.io.FileUtils;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.InetUtils;
import scw.net.message.converter.MultiMessageConverter;
import scw.value.property.PropertyFactory.DynamicValue;

public abstract class AbstractHttpClient implements HttpClient {
	static final DynamicValue<String> DEFAULT_UA = GlobalPropertyFactory.getInstance().getDynamicValue(
			"scw.http.client.headers.ua", String.class,
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
	static final ClientHttpResponseErrorHandler CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	static final HttpClientCookieManager COOKIE_MANAGER;

	static {
		ClientHttpResponseErrorHandler errorHandler = InstanceUtils.loadService(ClientHttpResponseErrorHandler.class);
		CLIENT_HTTP_RESPONSE_ERROR_HANDLER = errorHandler == null ? new DefaultClientHttpResponseErrorHandler()
				: errorHandler;

		COOKIE_MANAGER = InstanceUtils.loadService(HttpClientCookieManager.class);
	}

	protected final transient Logger logger = LoggerUtils.getLogger(getClass());
	private HttpClientCookieManager cookieManager = COOKIE_MANAGER;
	private ClientHttpResponseErrorHandler clientHttpResponseErrorHandler = CLIENT_HTTP_RESPONSE_ERROR_HANDLER;
	protected final MultiMessageConverter messageConverter = new MultiMessageConverter();
	//下载文件时是否支持重定向
	private boolean downloadSupportedRedirect = true;
	
	public AbstractHttpClient() {
		messageConverter.add(InetUtils.getMessageConverter());
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public boolean isDownloadSupportedRedirect() {
		return downloadSupportedRedirect;
	}

	public void setDownloadSupportedRedirect(boolean downloadSupportedRedirect) {
		this.downloadSupportedRedirect = downloadSupportedRedirect;
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

	public final <T> HttpResponseEntity<T> execute(URI uri, HttpMethod method, SSLSocketFactory sslSocketFactory, Object body,
			HttpHeaders httpHeaders, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		ClientHttpRequestBuilder requestBuilder = createBuilder(uri, method, sslSocketFactory);
		return execute(requestBuilder, createRequestBodyCallback(requestBuilder.getUri(), method, body, httpHeaders),
				clientResponseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(String url, HttpMethod method, SSLSocketFactory sslSocketFactory,
			Object body, HttpHeaders httpHeaders, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		return execute(InetUtils.toURI(url), method, sslSocketFactory, body, httpHeaders, clientResponseExtractor);
	}

	public final HttpResponseEntity<File> download(final File file, final URI uri, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		HttpResponseEntity<File> httpResponseEntity = execute(uri, HttpMethod.GET, sslSocketFactory, null, httpHeaders,
				new ClientHttpResponseExtractor<File>() {
					public File execute(ClientHttpResponse response) throws IOException {
						if (response.getStatusCode() != HttpStatus.OK) {
							logger.error("Unable to download:{}, status:{}, statusText:{}", uri,
									response.getRawStatusCode(), response.getStatusText());
							return null;
						}
						FileUtils.copyInputStreamToFile(response.getBody(), file);
						return file;
					}
				});
		if(isDownloadSupportedRedirect()){
			// 重定向
			if (httpResponseEntity.getStatusCodeValue() == HttpStatus.MOVED_PERMANENTLY.value()) {
				URI location = httpResponseEntity.getHeaders().getLocation();
				if (location != null) {
					logger.info("download redirect {} ==> {}", uri, location);
					return download(file, location, httpHeaders, sslSocketFactory);
				}
			}
		}
		return httpResponseEntity;
	}

	public final HttpResponseEntity<File> download(File file, String url, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return download(file, InetUtils.toURI(url), httpHeaders, sslSocketFactory);
	}

	protected abstract ClientHttpRequestBuilder createBuilder(URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory);

	protected ClientHttpRequestCallback createRequestBodyCallback(URI uri, HttpMethod httpMethod, final Object body,
			final HttpHeaders httpHeaders) {
		if (httpMethod == HttpMethod.GET && body != null) {
			logger.warn("Get request cannot set request body [{}]", uri);
		}

		final boolean canWrite = body != null && httpMethod != HttpMethod.GET;
		if (canWrite) {
			if (!getMessageConverter().canWrite(body, httpHeaders == null ? null : httpHeaders.getContentType())) {
				throw new NotSupportedException("not supported write body=" + body + ", headers=" + httpHeaders);
			}
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				clientRequest.getHeaders().set(HttpHeaders.USER_AGENT, DEFAULT_UA.getValue());
				if (httpHeaders != null) {
					clientRequest.getHeaders().putAll(httpHeaders);
				}

				if (canWrite) {
					getMessageConverter().write(body, httpHeaders == null ? null : httpHeaders.getContentType(),
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
			SSLSocketFactory sslSocketFactory, Object body, HttpHeaders httpHeaders) throws HttpClientException {
		return execute(responseType, InetUtils.toURI(url), method, sslSocketFactory, body, httpHeaders);
	}

	@SuppressWarnings("unchecked")
	public final <T> HttpResponseEntity<T> execute(Class<? extends T> responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, HttpHeaders httpHeaders) throws HttpClientException {
		return (HttpResponseEntity<T>) execute(uri, method, sslSocketFactory, body, httpHeaders,
				getClientHttpResponseExtractor(method, responseType));
	}

	public final HttpResponseEntity<Object> execute(Type responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, HttpHeaders httpHeaders) throws HttpClientException {
		return execute(responseType, InetUtils.toURI(url), method, sslSocketFactory, body, httpHeaders);
	}

	public final HttpResponseEntity<Object> execute(Type responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, HttpHeaders httpHeaders) throws HttpClientException {
		return execute(uri, method, sslSocketFactory, body, httpHeaders,
				getClientHttpResponseExtractor(method, responseType));
	}

	public final <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public final <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null, null);
	}

	public final HttpResponseEntity<Object> get(Type responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public final HttpResponseEntity<Object> get(Type responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null, null);
	}

	public final <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url, Object body,
			MediaType contentType) throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public final <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(contentType);
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, body, httpHeaders);
	}

	public final HttpResponseEntity<Object> post(Type responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public final HttpResponseEntity<Object> post(Type responseType, String url, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(contentType);
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, body, httpHeaders);
	}
}
