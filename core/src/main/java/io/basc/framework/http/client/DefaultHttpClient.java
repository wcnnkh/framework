package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.Configurable;
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

public class DefaultHttpClient extends AbstractHttpConnectionFactory implements HttpClient, Configurable {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
	protected final MessageConverters messageConverters = new DefaultMessageConverters();
	private final ClientHttpRequestInterceptors interceptors = new ClientHttpRequestInterceptors();

	public DefaultHttpClient() {
		super.setInterceptor(interceptors);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		interceptors.configure(serviceLoaderFactory);
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public final ClientHttpRequestInterceptors getInterceptors() {
		return interceptors;
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	protected void requestCallback(ClientHttpRequest request, ClientHttpRequestCallback requestCallback)
			throws IOException {
		if (requestCallback != null) {
			requestCallback.callback(request);
		}
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
		return execute(url, method, getRequestFactory(), requestCallback, responseExtractor);
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
		return execute(requestEntity, getRequestFactory(), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				TypeDescriptor.valueOf(responseType));
		return execute(requestEntity, getRequestFactory(), responseExtractor);
	}

	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, TypeDescriptor responseType)
			throws HttpClientException {
		ClientHttpResponseExtractor<T> responseExtractor = getClientHttpResponseExtractor(requestEntity.getMethod(),
				responseType);
		return execute(requestEntity, getRequestFactory(), responseExtractor);
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
			super(DefaultHttpClient.this);
		}

		public DefaultHttpConnection(HttpMethod method, URI url) {
			super(DefaultHttpClient.this, method, url);
		}

		public DefaultHttpConnection(DefaultHttpConnection httpConnection) {
			super(httpConnection);
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
