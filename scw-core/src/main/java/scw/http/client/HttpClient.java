package scw.http.client;

import java.io.IOException;
import java.net.URI;

import scw.convert.TypeDescriptor;
import scw.http.HttpMethod;
import scw.http.HttpRequestEntity;
import scw.http.HttpResponseEntity;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;

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
