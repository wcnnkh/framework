package scw.net.client.http;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.io.IOUtils;
import scw.net.client.AbstractClientOperations;
import scw.net.client.ClientRequestCallback;
import scw.net.client.ClientResponseExtractor;
import scw.net.client.http.exception.HttpClientException;
import scw.net.client.http.exception.HttpClientResourceAccessException;
import scw.net.http.MediaType;
import scw.net.http.HttpMethod;
import scw.net.http.SerializableHttpInputMessage;
import scw.net.message.InputMessage;

public abstract class AbstractHttpClient
		extends
		AbstractClientOperations<ClientHttpRequestBuilder, ClientHttpRequest, ClientHttpResponse>
		implements HttpClient {
	private ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler = new DefaultClientHttpInputMessageErrorHandler();

	public ClientHttpInputMessageErrorHandler getClientHttpInputMessageErrorHandler() {
		return clientHttpInputMessageErrorHandler;
	}

	public void setClientHttpInputMessageErrorHandler(
			ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler) {
		Assert.notNull(clientHttpInputMessageErrorHandler,
				"ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpInputMessageErrorHandler = clientHttpInputMessageErrorHandler;
	}

	@Override
	protected RuntimeException throwIOException(IOException ex,
			ClientHttpRequestBuilder builder) {
		return new HttpClientResourceAccessException("I/O error on "
				+ builder.getMethod().name() + " request for \""
				+ builder.getUri() + "\": " + ex.getMessage(), ex);
	}

	@Override
	protected SerializableHttpInputMessage convertToSerializableInputMessage(
			ClientHttpRequestBuilder builder, ClientHttpResponse response)
			throws IOException {
		byte[] body = builder.getMethod() == HttpMethod.OPTIONS ? null : IOUtils
				.toByteArray(response.getBody());
		return new SerializableHttpInputMessage(body, response.getHeaders());
	}

	@Override
	protected void handleResponse(ClientHttpRequestBuilder builder,
			ClientHttpResponse response) throws IOException {
		ClientHttpInputMessageErrorHandler errorHandler = getClientHttpInputMessageErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(builder.getMethod().name() + " request for \""
						+ builder.getUri() + "\" resulted in "
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

	@Override
	public SerializableHttpInputMessage execute(
			ClientHttpRequestBuilder builder, InputMessage inputMessage) {
		return (SerializableHttpInputMessage) super.execute(builder,
				inputMessage);
	}

	public SerializableHttpInputMessage getSerializableHttpInputMessage(
			String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return executeAndGetSerializableHttpInputMessage(url, HttpMethod.GET,
				sslSocketFactory, null, null);
	}

	public SerializableHttpInputMessage executeAndGetSerializableHttpInputMessage(
			String url, HttpMethod method, SSLSocketFactory sslSocketFactory,
			final Object body, final MediaType contentType)
			throws HttpClientException {
		final ClientHttpRequestBuilder builder = createBuilder(url, method,
				sslSocketFactory);
		return execute(
				builder,
				body == null ? null
						: new ClientRequestCallback<ClientHttpRequest>() {

							public void callback(ClientHttpRequest clientRequest)
									throws IOException {
								getMessageConverter().write(body, contentType,
										clientRequest);
							}
						},
				new ClientResponseExtractor<ClientHttpResponse, SerializableHttpInputMessage>() {

					public SerializableHttpInputMessage execute(
							ClientHttpResponse response) throws IOException {
						return convertToSerializableInputMessage(builder,
								response);
					}
				});
	}

	protected abstract ClientHttpRequestBuilder createBuilder(String url,
			HttpMethod method, SSLSocketFactory sslSocketFactory);

	public <T> T get(String url, Class<? extends T> responseType)
			throws HttpClientException {
		return get(url, responseType, null);
	}

	public <T> T get(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.GET, sslSocketFactory),
				responseType, null, null);
	}

	public Object get(String url, Type responseType) throws HttpClientException {
		return get(url, responseType, null);
	}

	public Object get(String url, Type responseType,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.GET, sslSocketFactory),
				responseType, null, null);
	}

	public <T> T post(String url, Class<? extends T> responseType, Object body,
			MediaType contentType) throws HttpClientException {
		return post(url, responseType, null, body, contentType);
	}

	public <T> T post(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.POST, sslSocketFactory),
				responseType, body, contentType);
	}

	public Object post(String url, Type responseType, Object body,
			MediaType contentType) throws HttpClientException {
		return post(url, responseType, null, body, contentType);
	}

	public Object post(String url, Type responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException {
		return execute(createBuilder(url, HttpMethod.POST, sslSocketFactory),
				responseType, body, contentType);
	}
}
