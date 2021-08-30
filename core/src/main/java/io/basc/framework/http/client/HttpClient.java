package io.basc.framework.http.client;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;

import java.io.IOException;
import java.net.URI;

/**
 * 一个http客户端
 * 
 * @author shuchaowen
 *
 */
public interface HttpClient extends HttpConnectionFactory {
	<T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException, IOException;

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			Class<T> responseType) throws HttpClientException, IOException;

	<T> HttpResponseEntity<T> execute(ClientHttpRequest request,
			TypeDescriptor responseType) throws HttpClientException, IOException;

	<T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, Class<T> responseType)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpRequestFactory requestFactory, TypeDescriptor responseType)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			Class<T> responseType) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			TypeDescriptor responseType) throws HttpClientException;

	<T> HttpResponseEntity<T> get(Class<T> responseType, String url)
			throws HttpClientException;

	<T> HttpResponseEntity<T> get(TypeDescriptor responseType, String url)
			throws HttpClientException;

	<T> HttpResponseEntity<T> post(Class<T> responseType, String url,
			Object body, MediaType contentType) throws HttpClientException;

	<T> HttpResponseEntity<T> post(TypeDescriptor responseType, String url, Object body,
			MediaType contentType) throws HttpClientException;
}
