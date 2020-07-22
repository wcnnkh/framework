package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.http.SerializableHttpInputMessage;
import scw.http.client.exception.HttpClientException;
import scw.http.client.exception.HttpClientResourceAccessException;
import scw.io.IOUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.MimeType;
import scw.net.InetUtils;
import scw.net.message.InputMessage;
import scw.net.message.converter.MultiMessageConverter;

public abstract class AbstractHttpClient implements HttpClient {
	private ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler = new DefaultClientHttpInputMessageErrorHandler();
	protected final transient Logger logger = LoggerUtils.getLogger(getClass());
	protected final MultiMessageConverter messageConverter = new MultiMessageConverter();

	public AbstractHttpClient() {
		messageConverter.add(InetUtils.getMessageConverter());
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public <T> T execute(ClientHttpRequestBuilder builder, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> clientResponseExtractor) throws HttpClientException {
		ClientHttpResponse response = null;
		ClientHttpRequest request;
		try {
			request = builder.builder();
			requestCallback(builder, request, requestCallback);
			response = request.execute();
			handleResponse(builder, response);
			return responseExtractor(builder, response, clientResponseExtractor);
		} catch (IOException ex) {
			throw throwIOException(ex, builder);
		} finally {
			if (response != null) {
				response.close();
			}
		}
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

	public SerializableHttpInputMessage execute(final ClientHttpRequestBuilder builder,
			final InputMessage inputMessage) {
		return execute(builder, new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				InetUtils.writeHeader(inputMessage, clientRequest);
				IOUtils.write(inputMessage.getBody(), clientRequest.getBody());
			}
		}, new ClientHttpResponseExtractor<SerializableHttpInputMessage>() {

			public SerializableHttpInputMessage execute(ClientHttpResponse response) throws IOException {
				return convertToSerializableInputMessage(builder, response);
			}
		});
	}

	public Object execute(ClientHttpRequestBuilder builder, final Type responseType, final Object body,
			final MimeType contentType) {
		return execute(builder, body == null ? null : new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				getMessageConverter().write(body, contentType, clientRequest);
			}
		}, new ClientHttpResponseExtractor<Object>() {

			public Object execute(ClientHttpResponse response) throws IOException {
				return getMessageConverter().read(responseType, response);
			}
		});
	}

	public <T> T execute(ClientHttpRequestBuilder builder, final Class<? extends T> responseType, final Object body,
			final MimeType contentType) {
		return execute(builder, new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				getMessageConverter().write(body, contentType, clientRequest);
			}
		}, new ClientHttpResponseExtractor<T>() {

			@SuppressWarnings("unchecked")
			public T execute(ClientHttpResponse response) throws IOException {
				return (T) getMessageConverter().read(responseType, response);
			}
		});
	}

	public ClientHttpInputMessageErrorHandler getClientHttpInputMessageErrorHandler() {
		return clientHttpInputMessageErrorHandler;
	}

	public void setClientHttpInputMessageErrorHandler(
			ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler) {
		Assert.notNull(clientHttpInputMessageErrorHandler, "ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpInputMessageErrorHandler = clientHttpInputMessageErrorHandler;
	}

	protected RuntimeException throwIOException(IOException ex, ClientHttpRequestBuilder builder) {
		return new HttpClientResourceAccessException("I/O error on " + builder.getMethod().name() + " request for \""
				+ builder.getUri() + "\": " + ex.getMessage(), ex);
	}

	protected SerializableHttpInputMessage convertToSerializableInputMessage(ClientHttpRequestBuilder builder,
			ClientHttpResponse response) throws IOException {
		byte[] body = builder.getMethod() == HttpMethod.OPTIONS ? null : IOUtils.toByteArray(response.getBody());
		return new SerializableHttpInputMessage(body, response.getHeaders());
	}

	protected void handleResponse(ClientHttpRequestBuilder builder, ClientHttpResponse response) throws IOException {
		ClientHttpInputMessageErrorHandler errorHandler = getClientHttpInputMessageErrorHandler();
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

	public SerializableHttpInputMessage getSerializableHttpInputMessage(String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return executeAndGetSerializableHttpInputMessage(url, HttpMethod.GET, sslSocketFactory, null, null);
	}

	public SerializableHttpInputMessage executeAndGetSerializableHttpInputMessage(String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, final Object body, final MediaType contentType)
			throws HttpClientException {
		final ClientHttpRequestBuilder builder = createBuilder(url, method, sslSocketFactory);
		return execute(builder, body == null ? null : new ClientHttpRequestCallback() {

			public void callback(ClientHttpRequest clientRequest) throws IOException {
				getMessageConverter().write(body, contentType, clientRequest);
			}
		}, new ClientHttpResponseExtractor<SerializableHttpInputMessage>() {

			public SerializableHttpInputMessage execute(ClientHttpResponse response) throws IOException {
				return convertToSerializableInputMessage(builder, response);
			}
		});
	}

	protected abstract ClientHttpRequestBuilder createBuilder(String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory);

	public <T> T get(String url, Class<? extends T> responseType) throws HttpClientException {
		return get(url, responseType, null);
	}

	public <T> T get(String url, Class<? extends T> responseType, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.GET, sslSocketFactory), responseType, null, null);
	}

	public Object get(String url, Type responseType) throws HttpClientException {
		return get(url, responseType, null);
	}

	public Object get(String url, Type responseType, SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.GET, sslSocketFactory), responseType, null, null);
	}

	public <T> T post(String url, Class<? extends T> responseType, Object body, MediaType contentType)
			throws HttpClientException {
		return post(url, responseType, null, body, contentType);
	}

	public <T> T post(String url, Class<? extends T> responseType, SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.POST, sslSocketFactory), responseType, body, contentType);
	}

	public Object post(String url, Type responseType, Object body, MediaType contentType) throws HttpClientException {
		return post(url, responseType, null, body, contentType);
	}

	public Object post(String url, Type responseType, SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.POST, sslSocketFactory), responseType, body, contentType);
	}
}
