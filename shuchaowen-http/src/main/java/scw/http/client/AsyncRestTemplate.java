package scw.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import scw.core.Assert;
import scw.core.ParameterizedTypeReference;
import scw.http.HttpEntity;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.ResponseEntity;
import scw.http.client.support.InterceptingAsyncHttpAccessor;
import scw.http.converter.HttpMessageConverter;
import scw.http.util.AbstractUriTemplateHandler;
import scw.http.util.UriTemplateHandler;
import scw.util.concurrent.ListenableFuture;
import scw.util.concurrent.ListenableFutureAdapter;
import scw.util.task.AsyncListenableTaskExecutor;
import scw.util.task.AsyncTaskExecutor;
import scw.util.task.SimpleAsyncTaskExecutor;

public class AsyncRestTemplate extends InterceptingAsyncHttpAccessor implements AsyncRestOperations {

	private final RestTemplate syncTemplate;


	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using default settings.
	 * <p>This constructor uses a {@link SimpleClientHttpRequestFactory} in combination
	 * with a {@link SimpleAsyncTaskExecutor} for asynchronous execution.
	 */
	public AsyncRestTemplate() {
		this(new SimpleAsyncTaskExecutor());
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncTaskExecutor}.
	 * <p>This constructor uses a {@link SimpleClientHttpRequestFactory} in combination
	 * with the given {@code AsyncTaskExecutor} for asynchronous execution.
	 */
	public AsyncRestTemplate(AsyncListenableTaskExecutor taskExecutor) {
		Assert.notNull(taskExecutor, "AsyncTaskExecutor must not be null");
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setTaskExecutor(taskExecutor);
		this.syncTemplate = new RestTemplate(requestFactory);
		setAsyncRequestFactory(requestFactory);
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncClientHttpRequestFactory}.
	 * <p>This constructor will cast the given asynchronous
	 * {@code AsyncClientHttpRequestFactory} to a {@link ClientHttpRequestFactory}. Since
	 * all implementations of {@code ClientHttpRequestFactory} provided in Spring also
	 * implement {@code AsyncClientHttpRequestFactory}, this should not result in a
	 * {@code ClassCastException}.
	 */
	public AsyncRestTemplate(AsyncClientHttpRequestFactory asyncRequestFactory) {
		this(asyncRequestFactory, (ClientHttpRequestFactory) asyncRequestFactory);
	}

	/**
	 * Creates a new instance of the {@code AsyncRestTemplate} using the given
	 * asynchronous and synchronous request factories.
	 * @param asyncRequestFactory the asynchronous request factory
	 * @param syncRequestFactory the synchronous request factory
	 */
	public AsyncRestTemplate(
			AsyncClientHttpRequestFactory asyncRequestFactory, ClientHttpRequestFactory syncRequestFactory) {

		this(asyncRequestFactory, new RestTemplate(syncRequestFactory));
	}

	/**
	 * Create a new instance of the {@code AsyncRestTemplate} using the given
	 * {@link AsyncClientHttpRequestFactory} and synchronous {@link RestTemplate}.
	 * @param requestFactory the asynchronous request factory to use
	 * @param restTemplate the synchronous template to use
	 */
	public AsyncRestTemplate(AsyncClientHttpRequestFactory requestFactory, RestTemplate restTemplate) {
		Assert.notNull(restTemplate, "RestTemplate must not be null");
		this.syncTemplate = restTemplate;
		setAsyncRequestFactory(requestFactory);
	}


	/**
	 * Set the error handler.
	 * <p>By default, AsyncRestTemplate uses a
	 * {@link org.springframework.web.client.DefaultResponseErrorHandler}.
	 */
	public void setErrorHandler(ResponseErrorHandler errorHandler) {
		this.syncTemplate.setErrorHandler(errorHandler);
	}

	/**
	 * Return the error handler.
	 */
	public ResponseErrorHandler getErrorHandler() {
		return this.syncTemplate.getErrorHandler();
	}

	/**
	 * Configure default URI variable values. This is a shortcut for:
	 * <pre class="code">
	 * DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler();
	 * handler.setDefaultUriVariables(...);
	 *
	 * AsyncRestTemplate restTemplate = new AsyncRestTemplate();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * @param defaultUriVariables the default URI variable values
	 * @since 4.3
	 */
	public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
		UriTemplateHandler handler = this.syncTemplate.getUriTemplateHandler();
		Assert.isInstanceOf(AbstractUriTemplateHandler.class, handler,
				"Can only use this property in conjunction with a DefaultUriTemplateHandler");
		((AbstractUriTemplateHandler) handler).setDefaultUriVariables(defaultUriVariables);
	}

	/**
	 * This property has the same purpose as the corresponding property on the
	 * {@code RestTemplate}. For more details see
	 * {@link RestTemplate#setUriTemplateHandler}.
	 * @param handler the URI template handler to use
	 */
	public void setUriTemplateHandler(UriTemplateHandler handler) {
		this.syncTemplate.setUriTemplateHandler(handler);
	}

	/**
	 * Return the configured URI template handler.
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.syncTemplate.getUriTemplateHandler();
	}

	public RestOperations getRestOperations() {
		return this.syncTemplate;
	}

	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.syncTemplate.setMessageConverters(messageConverters);
	}

	/**
	 * Return the message body converters.
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.syncTemplate.getMessageConverters();
	}


	// GET
	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(String url, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		AsyncRequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// HEAD
	public ListenableFuture<HttpHeaders> headForHeaders(String url, Object... uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
	}

	public ListenableFuture<HttpHeaders> headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor, uriVariables);
	}

	public ListenableFuture<HttpHeaders> headForHeaders(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		return execute(url, HttpMethod.HEAD, null, headersExtractor);
	}


	// POST
	public ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Object... uriVars)
			throws RestClientException {

		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
		return adaptToLocationHeader(future);
	}

	public ListenableFuture<URI> postForLocation(String url, HttpEntity<?> request, Map<String, ?> uriVars)
			throws RestClientException {

		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor, uriVars);
		return adaptToLocationHeader(future);
	}

	public ListenableFuture<URI> postForLocation(URI url, HttpEntity<?> request) throws RestClientException {
		AsyncRequestCallback callback = httpEntityCallback(request);
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.POST, callback, extractor);
		return adaptToLocationHeader(future);
	}

	private static ListenableFuture<URI> adaptToLocationHeader(ListenableFuture<HttpHeaders> future) {
		return new ListenableFutureAdapter<URI, HttpHeaders>(future) {
			@Override
			protected URI adapt(HttpHeaders headers) throws ExecutionException {
				return headers.getLocation();
			}
		};
	}

	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}
	
	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(String url, HttpEntity<?> request,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> postForEntity(URI url, HttpEntity<?> request, Class<T> responseType)
			throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}


	// PUT
	public ListenableFuture<?> put(String url, HttpEntity<?> request, Object... uriVariables) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	public ListenableFuture<?> put(String url, HttpEntity<?> request, Map<String, ?> uriVariables) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	public ListenableFuture<?> put(URI url, HttpEntity<?> request) throws RestClientException {
		AsyncRequestCallback requestCallback = httpEntityCallback(request);
		return execute(url, HttpMethod.PUT, requestCallback, null);
	}


	// DELETE
	public ListenableFuture<?> delete(String url, Object... uriVariables) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null, uriVariables);
	}

	public ListenableFuture<?> delete(String url, Map<String, ?> uriVariables) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null, uriVariables);
	}

	public ListenableFuture<?> delete(URI url) throws RestClientException {
		return execute(url, HttpMethod.DELETE, null, null);
	}


	// OPTIONS
	public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Object... uriVars) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
		return adaptToAllowHeader(future);
	}
	
	public ListenableFuture<Set<HttpMethod>> optionsForAllow(String url, Map<String, ?> uriVars) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor, uriVars);
		return adaptToAllowHeader(future);
	}

	public ListenableFuture<Set<HttpMethod>> optionsForAllow(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> extractor = headersExtractor();
		ListenableFuture<HttpHeaders> future = execute(url, HttpMethod.OPTIONS, null, extractor);
		return adaptToAllowHeader(future);
	}

	private static ListenableFuture<Set<HttpMethod>> adaptToAllowHeader(ListenableFuture<HttpHeaders> future) {
		return new ListenableFutureAdapter<Set<HttpMethod>, HttpHeaders>(future) {
			@Override
			protected Set<HttpMethod> adapt(HttpHeaders headers) throws ExecutionException {
				return headers.getAllow();
			}
		};
	}

	// exchange
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}
	
	public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ListenableFuture<ResponseEntity<T>> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		AsyncRequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}


	// general execution
	public <T> ListenableFuture<T> execute(String url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	public <T> ListenableFuture<T> execute(String url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	public <T> ListenableFuture<T> execute(URI url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI. The
	 * {@link org.springframework.http.client.ClientHttpRequest}
	 * is processed using the {@link RequestCallback}; the response with
	 * the {@link ResponseExtractor}.
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback object that prepares the request (can be {@code null})
	 * @param responseExtractor object that extracts the return value from the response (can
	 * be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	protected <T> ListenableFuture<T> doExecute(URI url, HttpMethod method, AsyncRequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		try {
			AsyncClientHttpRequest request = createAsyncRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			ListenableFuture<ClientHttpResponse> responseFuture = request.executeAsync();
			return new ResponseExtractorFuture<T>(method, url, responseFuture, responseExtractor);
		}
		catch (IOException ex) {
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + url + "\":" + ex.getMessage(), ex);
		}
	}

	private void logResponseStatus(HttpMethod method, URI url, ClientHttpResponse response) {
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Async " + method.name() + " request for \"" + url + "\" resulted in " +
						response.getRawStatusCode() + " (" + response.getStatusText() + ")");
			}
			catch (IOException ex) {
				// ignore
			}
		}
	}

	private void handleResponseError(HttpMethod method, URI url, ClientHttpResponse response) throws IOException {
		if (logger.isWarnEnabled()) {
			try {
				logger.warn("Async " + method.name() + " request for \"" + url + "\" resulted in " +
						response.getRawStatusCode() + " (" + response.getStatusText() + "); invoking error handler");
			}
			catch (IOException ex) {
				// ignore
			}
		}
		getErrorHandler().handleError(response);
	}

	/**
	 * Returns a request callback implementation that prepares the request {@code Accept}
	 * headers based on the given response type and configured {@linkplain
	 * #getMessageConverters() message converters}.
	 */
	protected <T> AsyncRequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.acceptHeaderRequestCallback(responseType));
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 */
	protected <T> AsyncRequestCallback httpEntityCallback(HttpEntity<T> requestBody) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(requestBody));
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 */
	protected <T> AsyncRequestCallback httpEntityCallback(HttpEntity<T> request, Type responseType) {
		return new AsyncRequestCallbackAdapter(this.syncTemplate.httpEntityCallback(request, responseType));
	}

	/**
	 * Returns a response extractor for {@link ResponseEntity}.
	 */
	protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
		return this.syncTemplate.responseEntityExtractor(responseType);
	}

	/**
	 * Returns a response extractor for {@link HttpHeaders}.
	 */
	protected ResponseExtractor<HttpHeaders> headersExtractor() {
		return this.syncTemplate.headersExtractor();
	}


	/**
	 * Future returned from
	 * {@link #doExecute(URI, HttpMethod, AsyncRequestCallback, ResponseExtractor)}
	 */
	private class ResponseExtractorFuture<T> extends ListenableFutureAdapter<T, ClientHttpResponse> {

		private final HttpMethod method;

		private final URI url;

		private final ResponseExtractor<T> responseExtractor;

		public ResponseExtractorFuture(HttpMethod method, URI url,
				ListenableFuture<ClientHttpResponse> clientHttpResponseFuture, ResponseExtractor<T> responseExtractor) {
			super(clientHttpResponseFuture);
			this.method = method;
			this.url = url;
			this.responseExtractor = responseExtractor;
		}

		@Override
		protected final T adapt(ClientHttpResponse response) throws ExecutionException {
			try {
				if (!getErrorHandler().hasError(response)) {
					logResponseStatus(this.method, this.url, response);
				}
				else {
					handleResponseError(this.method, this.url, response);
				}
				return convertResponse(response);
			}
			catch (Throwable ex) {
				throw new ExecutionException(ex);
			}
			finally {
				if (response != null) {
					response.close();
				}
			}
		}

		protected T convertResponse(ClientHttpResponse response) throws IOException {
			return (this.responseExtractor != null ? this.responseExtractor.extractData(response) : null);
		}
	}


	/**
	 * Adapts a {@link RequestCallback} to the {@link AsyncRequestCallback} interface.
	 */
	private static class AsyncRequestCallbackAdapter implements AsyncRequestCallback {

		private final RequestCallback adaptee;

		/**
		 * Create a new {@code AsyncRequestCallbackAdapter} from the given
		 * {@link RequestCallback}.
		 * @param requestCallback the callback to base this adapter on
		 */
		public AsyncRequestCallbackAdapter(RequestCallback requestCallback) {
			this.adaptee = requestCallback;
		}

		public void doWithRequest(final AsyncClientHttpRequest request) throws IOException {
			if (this.adaptee != null) {
				this.adaptee.doWithRequest(new ClientHttpRequest() {
					public ClientHttpResponse execute() throws IOException {
						throw new UnsupportedOperationException("execute not supported");
					}
					public OutputStream getBody() throws IOException {
						return request.getBody();
					}
					public HttpMethod getMethod() {
						return request.getMethod();
					}
					public URI getURI() {
						return request.getURI();
					}
					public HttpHeaders getHeaders() {
						return request.getHeaders();
					}
				});
			}
		}
	}

}
