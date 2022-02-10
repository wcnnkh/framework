package io.basc.framework.http.client;

import java.io.IOException;
import java.net.URI;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;

/**
 * 一个http客户端
 * 
 * @author shuchaowen
 *
 */
public interface HttpClient extends HttpClientExecutor {
	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException, IOException;

	ClientHttpRequestFactory getRequestFactory();

	/**
	 * 设置requestFactory并返回一个新的HttpClient
	 * 
	 * @param requestFactory 不能为空
	 * @return
	 */
	HttpClient setRequestFactory(ClientHttpRequestFactory requestFactory);

	<T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException;

	default <T> HttpResponseEntity<T> execute(ClientHttpRequest request, Class<T> responseType)
			throws HttpClientException, IOException {
		return execute(request, TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request, TypeDescriptor responseType)
			throws HttpClientException, IOException;

	default <T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, Class<T> responseType)
			throws HttpClientException {
		return execute(requestEntity, TypeDescriptor.valueOf(responseType));
	}

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity, TypeDescriptor responseType)
			throws HttpClientException;

	default <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, Class<T> responseType,
			ClientHttpRequestCallback requestCallback) throws HttpClientException {
		return execute(url, method, TypeDescriptor.valueOf(responseType), requestCallback);
	}

	<T> HttpResponseEntity<T> execute(URI url, HttpMethod method, TypeDescriptor responseType,
			ClientHttpRequestCallback requestCallback) throws HttpClientException;

	default <T> HttpResponseEntity<T> get(Class<T> responseType, String url) throws HttpClientException {
		return get(TypeDescriptor.valueOf(responseType), url);
	}

	<T> HttpResponseEntity<T> get(TypeDescriptor responseType, String url) throws HttpClientException;

	default <T> HttpResponseEntity<T> post(Class<T> responseType, String url, Object body, MediaType contentType)
			throws HttpClientException {
		return post(TypeDescriptor.valueOf(responseType), url, body, contentType);
	}

	<T> HttpResponseEntity<T> post(TypeDescriptor responseType, String url, Object body, MediaType contentType)
			throws HttpClientException;
}
