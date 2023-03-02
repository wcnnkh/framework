package io.basc.framework.http.client;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpRequestEntity.BodyBuilder;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.uri.UriUtils;

/**
 * 一个http客户端
 * 
 * @author wcnnkh
 *
 */
public interface HttpClient extends HttpClientConfigurable<HttpClient> {
	default HttpConnection createConnection(HttpMethod method, URI uri) {
		return createConnection(method.name(), uri);
	}

	default HttpConnection createConnection(HttpMethod method, String url) {
		return createConnection(method.name(), UriUtils.toUri(url));
	}

	default HttpConnection createConnection(String httpMethod, String url) {
		return createConnection(httpMethod, UriUtils.toUri(url));
	}

	HttpConnection createConnection(String httpMethod, URI uri);

	default HttpConnection createConnection(HttpMethod method, String uriTemplate, Map<String, ?> uriVariables) {
		return createConnection(method.name(), uriTemplate, uriVariables);
	}

	HttpConnection createConnection(String httpMethod, String uriTemplate, Map<String, ?> uriVariables);

	default HttpConnection createConnection(HttpMethod method, String uriTemplate, Object... uriVariables) {
		return createConnection(method.name(), uriTemplate, uriVariables);
	}

	HttpConnection createConnection(String httpMethod, String uriTemplate, Object... uriVariables);

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException {
		return execute(request, getCookieHandler(), responseExtractor);
	}

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request, Class<T> responseType)
			throws HttpClientException, IOException {
		return execute(request, getCookieHandler(), TypeDescriptor.valueOf(responseType));
	}

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request, TypeDescriptor responseType)
			throws HttpClientException, IOException {
		return execute(request, getCookieHandler(), responseType);
	}

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, @Nullable CookieHandler cookieHandler,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException, IOException;

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request, @Nullable CookieHandler cookieHandler,
			Class<? extends T> responseType) throws HttpClientException, IOException {
		return execute(request, cookieHandler, TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, @Nullable CookieHandler cookieHandler,
			TypeDescriptor responseType) throws HttpClientException, IOException;

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(uri, httpMethod, getRequestFactory(), getCookieHandler(), requestCallback, getRedirectManager(),
				responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			@Nullable CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback,
			@Nullable RedirectManager redirectManager, ClientHttpResponseExtractor<T> responseExtractor);

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			@Nullable CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback,
			@Nullable RedirectManager redirectManager, Class<? extends T> responseType) {
		return execute(uri, httpMethod, requestFactory, cookieHandler, requestCallback, redirectManager,
				TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			@Nullable CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback,
			@Nullable RedirectManager redirectManager, TypeDescriptor responseType);

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, Class<? extends T> responseType,
			ClientHttpRequestCallback requestCallback) {
		return execute(uri, httpMethod, getRequestFactory(), getCookieHandler(), requestCallback, getRedirectManager(),
				responseType);
	}

	default <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, TypeDescriptor responseType,
			ClientHttpRequestCallback requestCallback) {
		return execute(uri, httpMethod, getRequestFactory(), getCookieHandler(), requestCallback, getRedirectManager(),
				responseType);
	}

	default <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor) {
		return execute(requestEntity, getRequestFactory(), getCookieHandler(), getRedirectManager(), responseExtractor);
	}

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor);

	default <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, CookieHandler cookieHandler, RedirectManager redirectManager,
			Class<? extends T> responseType) {
		return execute(requestEntity, requestFactory, cookieHandler, redirectManager,
				TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, RedirectManager redirectManager, TypeDescriptor responseType);

	default <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws HttpClientException {
		return execute(requestEntity, TypeDescriptor.valueOf(responseType));
	}

	default <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, TypeDescriptor responseType)
			throws HttpClientException {
		return execute(requestEntity, getRequestFactory(), getCookieHandler(), getRedirectManager(), responseType);
	}

	default <T> HttpResponseEntity<T> get(Class<T> responseType, String url) throws HttpClientException {
		return get(TypeDescriptor.valueOf(responseType), url);
	}

	default <T> HttpResponseEntity<T> get(Class<T> responseType, URI uri) throws HttpClientException {
		return get(TypeDescriptor.valueOf(responseType), uri);
	}

	default <T> HttpResponseEntity<T> get(TypeDescriptor responseType, String url) throws HttpClientException {
		return get(responseType, UriUtils.toUri(url));
	}

	default <T> HttpResponseEntity<T> get(TypeDescriptor responseType, URI uri) throws HttpClientException {
		return execute(HttpRequestEntity.get(uri).build(), responseType);
	}

	default <T> HttpResponseEntity<T> post(Class<T> responseType, URI uri, Object body, MediaType contentType)
			throws HttpClientException {
		return post(TypeDescriptor.valueOf(responseType), uri, body, contentType);
	}

	default <T> HttpResponseEntity<T> post(Class<T> responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(TypeDescriptor.valueOf(responseType), UriUtils.toUri(url), body, contentType);
	}

	default <T> HttpResponseEntity<T> post(TypeDescriptor responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(responseType, UriUtils.toUri(url), body, contentType);
	}

	default <T> HttpResponseEntity<T> post(TypeDescriptor responseType, URI uri, Object body, MediaType contentType)
			throws HttpClientException {
		BodyBuilder<?> bodyBuilder = HttpRequestEntity.post(uri);
		if (contentType != null) {
			bodyBuilder = bodyBuilder.contentType(contentType);
		}
		return execute(bodyBuilder.body(body), responseType);
	}
}
