package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;

import scw.core.Assert;
import scw.core.ParameterizedTypeReference;
import scw.http.HttpEntity;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.http.RequestEntity;
import scw.http.ResponseEntity;
import scw.http.converter.ByteArrayHttpMessageConverter;
import scw.http.converter.GenericHttpMessageConverter;
import scw.http.converter.HttpMessageConverter;
import scw.http.converter.ResourceHttpMessageConverter;
import scw.http.converter.StringHttpMessageConverter;
import scw.http.converter.json.JsonHttpMessageConverter;
import scw.http.converter.xml.SourceHttpMessageConverter;
import scw.http.util.AbstractUriTemplateHandler;
import scw.http.util.DefaultUriTemplateHandler;
import scw.http.util.UriTemplateHandler;

public class RestTemplate extends InterceptingHttpAccessor implements RestOperations {
	private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	private UriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();

	private final ResponseExtractor<HttpHeaders> headersExtractor = new HeadersExtractor();


	/**
	 * Create a new instance of the {@link RestTemplate} using default settings.
	 * Default {@link HttpMessageConverter}s are initialized.
	 */
	public RestTemplate() {
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(new StringHttpMessageConverter());
		this.messageConverters.add(new ResourceHttpMessageConverter());
		this.messageConverters.add(new SourceHttpMessageConverter<Source>());
		this.messageConverters.add(new JsonHttpMessageConverter());
	}

	/**
	 * Create a new instance of the {@link RestTemplate} based on the given {@link ClientHttpRequestFactory}.
	 * @param requestFactory HTTP request factory to use
	 * @see org.springframework.http.client.SimpleClientHttpRequestFactory
	 * @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory
	 */
	public RestTemplate(ClientHttpRequestFactory requestFactory) {
		this();
		setRequestFactory(requestFactory);
	}

	/**
	 * Create a new instance of the {@link RestTemplate} using the given list of
	 * {@link HttpMessageConverter} to use
	 * @param messageConverters the list of {@link HttpMessageConverter} to use
	 * @since 3.2.7
	 */
	public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
		this.messageConverters.addAll(messageConverters);
	}


	/**
	 * Set the message body converters to use.
	 * <p>These converters are used to convert from and to HTTP requests and responses.
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		Assert.notEmpty(messageConverters, "At least one HttpMessageConverter required");
		// Take getMessageConverters() List as-is when passed in here
		if (this.messageConverters != messageConverters) {
			this.messageConverters.clear();
			this.messageConverters.addAll(messageConverters);
		}
	}

	/**
	 * Return the list of message body converters.
	 * <p>The returned {@link List} is active and may get appended to.
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	/**
	 * Set the error handler.
	 * <p>By default, RestTemplate uses a {@link DefaultResponseErrorHandler}.
	 */
	public void setErrorHandler(ResponseErrorHandler errorHandler) {
		Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
		this.errorHandler = errorHandler;
	}

	/**
	 * Return the error handler.
	 */
	public ResponseErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	/**
	 * Configure default URI variable values. This is a shortcut for:
	 * <pre class="code">
	 * DefaultUriTemplateHandler handler = new DefaultUriTemplateHandler();
	 * handler.setDefaultUriVariables(...);
	 *
	 * RestTemplate restTemplate = new RestTemplate();
	 * restTemplate.setUriTemplateHandler(handler);
	 * </pre>
	 * @param defaultUriVariables the default URI variable values
	 * @since 4.3
	 */
	public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
		Assert.isInstanceOf(AbstractUriTemplateHandler.class, this.uriTemplateHandler,
				"Can only use this property in conjunction with an AbstractUriTemplateHandler");
		((AbstractUriTemplateHandler) this.uriTemplateHandler).setDefaultUriVariables(defaultUriVariables);
	}

	/**
	 * Configure the {@link UriTemplateHandler} to use to expand URI templates.
	 * By default the {@link DefaultUriTemplateHandler} is used which relies on
	 * Spring's URI template support and exposes several useful properties that
	 * customize its behavior for encoding and for prepending a common base URL.
	 * An alternative implementation may be used to plug an external URI
	 * template library.
	 * @param handler the URI template handler to use
	 */
	public void setUriTemplateHandler(UriTemplateHandler handler) {
		Assert.notNull(handler, "UriTemplateHandler must not be null");
		this.uriTemplateHandler = handler;
	}

	/**
	 * Return the configured URI template handler.
	 */
	public UriTemplateHandler getUriTemplateHandler() {
		return this.uriTemplateHandler;
	}


	// GET
	public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}
	
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = acceptHeaderRequestCallback(responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
	}


	// HEAD

	public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor(), uriVariables);
	}

	public HttpHeaders headForHeaders(URI url) throws RestClientException {
		return execute(url, HttpMethod.HEAD, null, headersExtractor());
	}


	// POST

	public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
		return headers.getLocation();
	}

	public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor(), uriVariables);
		return headers.getLocation();
	}

	public URI postForLocation(URI url, Object request) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		HttpHeaders headers = execute(url, HttpMethod.POST, requestCallback, headersExtractor());
		return headers.getLocation();
	}

	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}

	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, HttpMethod.POST, requestCallback, responseExtractor);
	}


	// PUT
	public void put(String url, Object request, Object... uriVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null, uriVariables);
	}

	public void put(URI url, Object request) throws RestClientException {
		RequestCallback requestCallback = httpEntityCallback(request);
		execute(url, HttpMethod.PUT, requestCallback, null);
	}


	// PATCH
	public <T> T patchForObject(String url, Object request, Class<T> responseType,
			Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T patchForObject(String url, Object request, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
		return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor, uriVariables);
	}

	public <T> T patchForObject(URI url, Object request, Class<T> responseType)
			throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(request, responseType);
		HttpMessageConverterExtractor<T> responseExtractor =
				new HttpMessageConverterExtractor<T>(responseType, getMessageConverters());
		return execute(url, HttpMethod.PATCH, requestCallback, responseExtractor);
	}


	// DELETE
	public void delete(String url, Object... uriVariables) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null, uriVariables);
	}

	public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null, uriVariables);
	}

	public void delete(URI url) throws RestClientException {
		execute(url, HttpMethod.DELETE, null, null);
	}


	// OPTIONS
	public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
		return headers.getAllow();
	}

	public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor, uriVariables);
		return headers.getAllow();
	}

	public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
		ResponseExtractor<HttpHeaders> headersExtractor = headersExtractor();
		HttpHeaders headers = execute(url, HttpMethod.OPTIONS, null, headersExtractor);
		return headers.getAllow();
	}


	// exchange

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor, uriVariables);
	}

	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(url, method, requestCallback, responseExtractor);
	}

	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {

		Assert.notNull(requestEntity, "RequestEntity must not be null");

		RequestCallback requestCallback = httpEntityCallback(requestEntity, responseType);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(responseType);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}

	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {

		Assert.notNull(requestEntity, "RequestEntity must not be null");

		Type type = responseType.getType();
		RequestCallback requestCallback = httpEntityCallback(requestEntity, type);
		ResponseExtractor<ResponseEntity<T>> responseExtractor = responseEntityExtractor(type);
		return execute(requestEntity.getUrl(), requestEntity.getMethod(), requestCallback, responseExtractor);
	}


	// general execution
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {

		URI expanded = getUriTemplateHandler().expand(url, uriVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		return doExecute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Execute the given method on the provided URI.
	 * <p>The {@link ClientHttpRequest} is processed using the {@link RequestCallback};
	 * the response with the {@link ResponseExtractor}.
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param requestCallback object that prepares the request (can be {@code null})
	 * @param responseExtractor object that extracts the return value from the response (can be {@code null})
	 * @return an arbitrary object, as returned by the {@link ResponseExtractor}
	 */
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = createRequest(url, method);
			if (requestCallback != null) {
				requestCallback.doWithRequest(request);
			}
			response = request.execute();
			handleResponse(url, method, response);
			if (responseExtractor != null) {
				return responseExtractor.extractData(response);
			}
			else {
				return null;
			}
		}
		catch (IOException ex) {
			String resource = url.toString();
			String query = url.getRawQuery();
			resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
			throw new ResourceAccessException("I/O error on " + method.name() +
					" request for \"" + resource + "\": " + ex.getMessage(), ex);
		}
		finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * Handle the given response, performing appropriate logging and
	 * invoking the {@link ResponseErrorHandler} if necessary.
	 * <p>Can be overridden in subclasses.
	 * @param url the fully-expanded URL to connect to
	 * @param method the HTTP method to execute (GET, POST, etc.)
	 * @param response the resulting {@link ClientHttpResponse}
	 * @throws IOException if propagated from {@link ResponseErrorHandler}
	 * @since 4.1.6
	 * @see #setErrorHandler
	 */
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		ResponseErrorHandler errorHandler = getErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(method.name() + " request for \"" + url + "\" resulted in " +
						response.getRawStatusCode() + " (" + response.getStatusText() + ")" +
						(hasError ? "; invoking error handler" : ""));
			}
			catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	/**
	 * Returns a request callback implementation that prepares the request {@code Accept}
	 * headers based on the given response type and configured
	 * {@linkplain #getMessageConverters() message converters}.
	 */
	protected <T> RequestCallback acceptHeaderRequestCallback(Class<T> responseType) {
		return new AcceptHeaderRequestCallback(responseType);
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 */
	protected <T> RequestCallback httpEntityCallback(Object requestBody) {
		return new HttpEntityRequestCallback(requestBody);
	}

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 */
	protected <T> RequestCallback httpEntityCallback(Object requestBody, Type responseType) {
		return new HttpEntityRequestCallback(requestBody, responseType);
	}

	/**
	 * Returns a response extractor for {@link ResponseEntity}.
	 */
	protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
		return new ResponseEntityResponseExtractor<T>(responseType);
	}

	/**
	 * Returns a response extractor for {@link HttpHeaders}.
	 */
	protected ResponseExtractor<HttpHeaders> headersExtractor() {
		return this.headersExtractor;
	}


	/**
	 * Request callback implementation that prepares the request's accept headers.
	 */
	private class AcceptHeaderRequestCallback implements RequestCallback {

		private final Type responseType;

		private AcceptHeaderRequestCallback(Type responseType) {
			this.responseType = responseType;
		}

		public void doWithRequest(ClientHttpRequest request) throws IOException {
			if (this.responseType != null) {
				Class<?> responseClass = null;
				if (this.responseType instanceof Class) {
					responseClass = (Class<?>) this.responseType;
				}
				Set<MediaType> allSupportedMediaTypes = new LinkedHashSet<MediaType>();
				for (HttpMessageConverter<?> converter : getMessageConverters()) {
					if (responseClass != null) {
						if (converter.canRead(responseClass, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					}
					else if (converter instanceof GenericHttpMessageConverter) {
						GenericHttpMessageConverter<?> genericConverter = (GenericHttpMessageConverter<?>) converter;
						if (genericConverter.canRead(this.responseType, null, null)) {
							allSupportedMediaTypes.addAll(getSupportedMediaTypes(converter));
						}
					}
				}
				if (!allSupportedMediaTypes.isEmpty()) {
					List<MediaType> result = new ArrayList<MediaType>(allSupportedMediaTypes);
					MediaType.sortBySpecificity(result);
					if (logger.isDebugEnabled()) {
						logger.debug("Setting request Accept header to " + allSupportedMediaTypes);
					}
					request.getHeaders().setAccept(result);
				}
			}
		}

		private List<MediaType> getSupportedMediaTypes(HttpMessageConverter<?> messageConverter) {
			List<MediaType> supportedMediaTypes = messageConverter.getSupportedMediaTypes();
			List<MediaType> result = new ArrayList<MediaType>(supportedMediaTypes.size());
			for (MediaType supportedMediaType : supportedMediaTypes) {
				if (supportedMediaType.getCharset() != null) {
					supportedMediaType =
							new MediaType(supportedMediaType.getType(), supportedMediaType.getSubtype());
				}
				result.add(supportedMediaType);
			}
			return result;
		}
	}


	/**
	 * Request callback implementation that writes the given object to the request stream.
	 */
	private class HttpEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final HttpEntity<?> requestEntity;

		private HttpEntityRequestCallback(Object requestBody) {
			this(requestBody, null);
		}

		private HttpEntityRequestCallback(Object requestBody, Type responseType) {
			super(responseType);
			if (requestBody instanceof HttpEntity) {
				this.requestEntity = (HttpEntity<?>) requestBody;
			}
			else if (requestBody != null) {
				this.requestEntity = new HttpEntity<Object>(requestBody);
			}
			else {
				this.requestEntity = HttpEntity.EMPTY;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void doWithRequest(ClientHttpRequest httpRequest) throws IOException {
			super.doWithRequest(httpRequest);
			if (!this.requestEntity.hasBody()) {
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				if (!requestHeaders.isEmpty()) {
					for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
						httpHeaders.put(entry.getKey(), new LinkedList<String>(entry.getValue()));
					}
				}
				if (httpHeaders.getContentLength() < 0) {
					httpHeaders.setContentLength(0L);
				}
			}
			else {
				Object requestBody = this.requestEntity.getBody();
				Class<?> requestBodyClass = requestBody.getClass();
				Type requestBodyType = (this.requestEntity instanceof RequestEntity ?
						((RequestEntity<?>)this.requestEntity).getType() : requestBodyClass);
				HttpHeaders httpHeaders = httpRequest.getHeaders();
				HttpHeaders requestHeaders = this.requestEntity.getHeaders();
				MediaType requestContentType = requestHeaders.getContentType();
				for (HttpMessageConverter<?> messageConverter : getMessageConverters()) {
					if (messageConverter instanceof GenericHttpMessageConverter) {
						GenericHttpMessageConverter<Object> genericMessageConverter = (GenericHttpMessageConverter<Object>) messageConverter;
						if (genericMessageConverter.canWrite(requestBodyType, requestBodyClass, requestContentType)) {
							if (!requestHeaders.isEmpty()) {
								for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
									httpHeaders.put(entry.getKey(), new LinkedList<String>(entry.getValue()));
								}
							}
							if (logger.isDebugEnabled()) {
								if (requestContentType != null) {
									logger.debug("Writing [" + requestBody + "] as \"" + requestContentType +
											"\" using [" + messageConverter + "]");
								}
								else {
									logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
								}

							}
							genericMessageConverter.write(
									requestBody, requestBodyType, requestContentType, httpRequest);
							return;
						}
					}
					else if (messageConverter.canWrite(requestBodyClass, requestContentType)) {
						if (!requestHeaders.isEmpty()) {
							for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
								httpHeaders.put(entry.getKey(), new LinkedList<String>(entry.getValue()));
							}
						}
						if (logger.isDebugEnabled()) {
							if (requestContentType != null) {
								logger.debug("Writing [" + requestBody + "] as \"" + requestContentType +
										"\" using [" + messageConverter + "]");
							}
							else {
								logger.debug("Writing [" + requestBody + "] using [" + messageConverter + "]");
							}

						}
						((HttpMessageConverter<Object>) messageConverter).write(
								requestBody, requestContentType, httpRequest);
						return;
					}
				}
				String message = "Could not write request: no suitable HttpMessageConverter found for request type [" +
						requestBodyClass.getName() + "]";
				if (requestContentType != null) {
					message += " and content type [" + requestContentType + "]";
				}
				throw new RestClientException(message);
			}
		}
	}


	/**
	 * Response extractor for {@link HttpEntity}.
	 */
	private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<ResponseEntity<T>> {

		private final HttpMessageConverterExtractor<T> delegate;

		public ResponseEntityResponseExtractor(Type responseType) {
			if (responseType != null && Void.class != responseType) {
				this.delegate = new HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
			}
			else {
				this.delegate = null;
			}
		}

		public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
			if (this.delegate != null) {
				T body = this.delegate.extractData(response);
				return ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).body(body);
			}
			else {
				return ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).build();
			}
		}
	}


	/**
	 * Response extractor that extracts the response {@link HttpHeaders}.
	 */
	private static class HeadersExtractor implements ResponseExtractor<HttpHeaders> {

		public HttpHeaders extractData(ClientHttpResponse response) throws IOException {
			return response.getHeaders();
		}
	}

}
