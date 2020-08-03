package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.HttpStatus;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;
import scw.http.client.exception.HttpClientResourceAccessException;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.InetUtils;
import scw.net.message.converter.MultiMessageConverter;

public abstract class AbstractHttpClient implements HttpClient {
	private ClientHttpResponseErrorHandler clientHttpResponseErrorHandler = new DefaultClientHttpInputMessageErrorHandler();
	protected final transient Logger logger = LoggerUtils.getLogger(getClass());
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

	public <T> HttpResponseEntity<T> execute(ClientHttpRequestBuilder builder,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException {
		ClientHttpResponse response = null;
		ClientHttpRequest request;
		try {
			request = builder.builder();
			requestCallback(builder, request, requestCallback);
			response = request.execute();
			handleResponse(builder, response);
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

	protected abstract ClientHttpRequestBuilder createBuilder(String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory);

	protected ClientHttpRequestCallback getWriteRequestBodyCallback(final HttpMethod httpMethod, final Object body,
			final MediaType contentType) {
		if (body == null || httpMethod == HttpMethod.GET) {
			return null;
		}

		if (!getMessageConverter().canWrite(body, contentType)) {
			throw new NotSupportedException("not supported write contentType=" + contentType + ", body=" + body);
		}

		return new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				getMessageConverter().write(body, contentType, clientRequest);
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

	public HttpResponseEntity<Object> execute(final Type responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException {
		return execute(createBuilder(url, method, sslSocketFactory),
				getWriteRequestBodyCallback(method, body, contentType),
				getClientHttpResponseExtractor(method, responseType));
	}

	@SuppressWarnings("unchecked")
	public <T> HttpResponseEntity<T> execute(final Class<? extends T> responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, final Object body, final MediaType contentType)
			throws HttpClientException {
		return (HttpResponseEntity<T>) execute(createBuilder(url, method, sslSocketFactory),
				getWriteRequestBodyCallback(method, body, contentType),
				getClientHttpResponseExtractor(method, responseType));
	}

	public <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public <T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null, null);
	}

	public HttpResponseEntity<Object> get(Type responseType, String url) throws HttpClientException {
		return get(responseType, url, null);
	}

	public HttpResponseEntity<Object> get(Type responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(responseType, url, HttpMethod.GET, sslSocketFactory, null, null);
	}

	public <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url, Object body,
			MediaType contentType) throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public <T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException {
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, body, contentType);
	}

	public HttpResponseEntity<Object> post(Type responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(responseType, url, null, body, contentType);
	}

	public HttpResponseEntity<Object> post(Type responseType, String url, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException {
		return execute(responseType, url, HttpMethod.POST, sslSocketFactory, body, contentType);
	}
}
