package run.soeasy.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpRequestEntity;
import run.soeasy.framework.http.HttpResponseEntity;
import run.soeasy.framework.net.convert.MessageConverter;
import run.soeasy.framework.net.uri.DefaultUriTemplateHandler;
import run.soeasy.framework.net.uri.UriTemplateHandler;
import run.soeasy.framework.retry.RetryOperations;
import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.collections.CollectionUtils;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.spi.Configurable;
import run.soeasy.framework.util.spi.ServiceLoaderDiscovery;

public abstract class AbstractHttpClient implements HttpClient, Configurable {
	private ClientHttpRequestFactory requestFactory;
	private CookieHandler cookieHandler;
	private RedirectManager redirectManager;
	private boolean cloneBeforeSet = true;
	private UriTemplateHandler uriTemplateHandler;
	private ClientHttpResponseErrorHandler responseErrorHandler;
	private ClientHttpRequestInterceptor interceptor;
	private MessageConverter messageConverter;
	private RetryOperations retryOperations;

	public AbstractHttpClient(ClientHttpRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public AbstractHttpClient(AbstractHttpClient configurable) {
		this.requestFactory = configurable.requestFactory;
		this.cookieHandler = configurable.cookieHandler;
		this.redirectManager = configurable.redirectManager;
		this.cloneBeforeSet = configurable.cloneBeforeSet;
		this.uriTemplateHandler = configurable.uriTemplateHandler;
		this.responseErrorHandler = configurable.responseErrorHandler;
		this.interceptor = configurable.interceptor;
		this.messageConverter = configurable.messageConverter;
	}

	public boolean isCloneBeforeSet() {
		return cloneBeforeSet;
	}

	public void setCloneBeforeSet(boolean cloneBeforeSet) {
		this.cloneBeforeSet = cloneBeforeSet;
	}

	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		this.retryOperations = retryOperations;
	}

	public abstract AbstractHttpClient clone();

	@Override
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		discovery.getServiceLoader(ClientHttpRequestFactory.class).findFirst()
				.ifPresent((e) -> this.requestFactory = e);
		discovery.getServiceLoader(CookieHandler.class).findFirst().ifPresent((e) -> this.cookieHandler = e);
		discovery.getServiceLoader(RedirectManager.class).findFirst().ifPresent((e) -> this.redirectManager = e);
		discovery.getServiceLoader(UriTemplateHandler.class).findFirst().ifPresent((e) -> this.uriTemplateHandler = e);
		discovery.getServiceLoader(ClientHttpResponseErrorHandler.class).findFirst()
				.ifPresent((e) -> this.responseErrorHandler = e);
		discovery.getServiceLoader(RetryOperations.class).findFirst().ifPresent((e) -> this.retryOperations = e);
		return Receipt.SUCCESS;
	}

	public UriTemplateHandler getUriTemplateHandler() {
		return uriTemplateHandler == null ? DefaultUriTemplateHandler.INSTANCE : uriTemplateHandler;
	}

	public void setUriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
		this.uriTemplateHandler = uriTemplateHandler;
	}

	public ClientHttpResponseErrorHandler getResponseErrorHandler() {
		return responseErrorHandler;
	}

	public void setResponseErrorHandler(ClientHttpResponseErrorHandler responseErrorHandler) {
		this.responseErrorHandler = responseErrorHandler;
	}

	public ClientHttpRequestInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(ClientHttpRequestInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public ClientHttpRequestFactory getRequestFactory() {
		return requestFactory;
	}

	@Override
	public AbstractHttpClient setRequestFactory(ClientHttpRequestFactory requestFactory) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.requestFactory = requestFactory;
		return client;
	}

	@Override
	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	@Override
	public AbstractHttpClient setCookieHandler(CookieHandler cookieHandler) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.cookieHandler = cookieHandler;
		return client;
	}

	@Override
	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	@Override
	public AbstractHttpClient setRedirectManager(RedirectManager redirectManager) {
		AbstractHttpClient client = isCloneBeforeSet() ? clone() : this;
		client.redirectManager = redirectManager;
		return client;
	}

	@Override
	public HttpConnection createConnection(String httpMethod, URI uri) {
		HttpClientConnection connection = new HttpClientConnection(uri, httpMethod, this);
		connection.setCloneBeforeSet(true);
		return connection;
	}

	@Override
	public final HttpConnection createConnection(String httpMethod, String uriTemplate, Map<String, ?> uriVariables) {
		URI uri = getUriTemplateHandler().expand(uriTemplate, uriVariables);
		return createConnection(httpMethod, uri);
	}

	@Override
	public final HttpConnection createConnection(String httpMethod, String uriTemplate, Object... uriVariables) {
		URI uri = getUriTemplateHandler().expand(uriTemplate, uriVariables);
		return createConnection(httpMethod, uri);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(request, cookieHandler, responseExtractor, null);
	}

	protected <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType)
			throws HttpClientException, IOException {
		ClientHttpResponse response = null;
		if (cookieHandler != null) {
			Map<String, List<String>> map = cookieHandler.get(request.getURI(), request.getHeaders());
			if (!CollectionUtils.isEmpty(map)) {
				request.getHeaders().addAll(map);
			}
		}

		ClientHttpRequestInterceptor interceptor = getInterceptor();
		if (interceptor == null) {
			response = request.execute();
		} else {
			ClientHttpRequestInterceptorChain chain = new ClientHttpRequestInterceptorChain(
					Arrays.asList(interceptor).iterator());
			response = chain.execute(request);
		}

		handleResponse(request, response);
		if (cookieHandler != null) {
			cookieHandler.put(request.getURI(), response.getHeaders());
		}

		T body = responseExtractor(request, response, responseExtractor);
		return new HttpResponseEntity<T>(body, responseType, response.getHeaders(), response.getStatusCode());
	}

	protected void handleResponse(ClientHttpRequest request, ClientHttpResponse response) throws IOException {
		ClientHttpResponseErrorHandler errorHandler = getResponseErrorHandler();
		if (errorHandler == null) {
			return;
		}

		boolean hasError = errorHandler.hasError(response);
		if (hasError) {
			errorHandler.handleError(response);
		}
	}

	protected <T> T responseExtractor(ClientHttpRequest request, ClientHttpResponse response,
			ClientHttpResponseExtractor<T> clientResponseExtractor) throws IOException {
		return clientResponseExtractor == null ? null : clientResponseExtractor.execute(request, response);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, null);
	}

	protected <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, responseType, 0);
	}

	private <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType, long deep) {
		KeyValue<HttpRequest, HttpResponseEntity<T>> response;
		RetryOperations retryOperations = getRetryOperations();
		if (retryOperations == null) {
			response = executeInternal(uri, httpMethod, requestFactory, cookieHandler, requestCallback,
					responseExtractor, responseType);
		} else {
			// 支持重试
			response = retryOperations.execute((context) -> {
				try {
					return executeInternal(uri, httpMethod, requestFactory, cookieHandler, requestCallback,
							responseExtractor, responseType);
				} catch (HttpClientErrorException e) {
					// 只有出现客户端异常时才重试
					throw e;
				} catch (Throwable e) {
					// 其他异常不再重试，结束
					context.setExhaustedOnly();
					throw e;
				}
			});
		}

		if (redirectManager == null) {
			return response.getValue();
		}

		URI redirectUri = redirectManager.getRedirect(response.getKey(), response.getValue(), deep);
		if (redirectUri == null) {
			return response.getValue();
		}

		return execute(redirectUri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				responseExtractor, responseType, deep + 1);
	}

	private <T> KeyValue<HttpRequest, HttpResponseEntity<T>> executeInternal(URI uri, String httpMethod,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor,
			TypeDescriptor responseType) throws HttpClientException {
		ClientHttpRequest request;
		try {
			request = requestFactory.createRequest(uri, httpMethod);
			request = requestCallback(request, requestCallback);
			HttpResponseEntity<T> responseEntity = execute(request, cookieHandler, responseExtractor, responseType);
			return KeyValue.of(request, responseEntity);
		} catch (IOException e) {
			throw new HttpClientResourceAccessException(
					"I/O error on " + httpMethod + " request for \"" + uri + "\": " + e.getMessage(), e);
		}
	}

	protected ClientHttpRequest requestCallback(ClientHttpRequest request, ClientHttpRequestCallback requestCallback)
			throws IOException {
		if (requestCallback != null) {
			return requestCallback.callback(request);
		}
		return request;
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(ClientHttpRequest request, CookieHandler cookieHandler,
			TypeDescriptor responseType) throws HttpClientException, IOException {
		return execute(request, cookieHandler,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(requestEntity.getURI(), requestEntity.getRawMethod(), requestFactory, cookieHandler,
				new MessageConverterClientHttpRequestCallback(getMessageConverter(), requestEntity), redirectManager,
				responseExtractor);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler, RedirectManager redirectManager,
			TypeDescriptor responseType) {
		return execute(requestEntity.getURI(), requestEntity.getRawMethod(), requestFactory, cookieHandler,
				new MessageConverterClientHttpRequestCallback(getMessageConverter(), requestEntity), redirectManager,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}

	@Override
	public final <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			TypeDescriptor responseType) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				new MessageConverterClientHttpResponseExtractor<>(getMessageConverter(), responseType), responseType);
	}
}
