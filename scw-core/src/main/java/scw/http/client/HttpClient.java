package scw.http.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;

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
			Type responseType) throws HttpClientException, IOException;

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
			ClientHttpRequestFactory requestFactory, Type responseType)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			Class<T> responseType) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(HttpRequestEntity<?> requestEntity,
			Type responseType) throws HttpClientException;

	<T> HttpResponseEntity<T> get(Class<T> responseType, String url)
			throws HttpClientException;

	HttpResponseEntity<Object> get(Type responseType, String url)
			throws HttpClientException;

	<T> HttpResponseEntity<T> post(Class<T> responseType, String url,
			Object body, MediaType contentType) throws HttpClientException;

	HttpResponseEntity<Object> post(Type responseType, String url, Object body,
			MediaType contentType) throws HttpClientException;
}
