package scw.http.client;

import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;

/**
 * 一个http客户端
 * 
 * @author shuchaowen
 *
 */
public interface HttpClient {

	<T> HttpResponseEntity<T> execute(ClientHttpRequestBuilder builder, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> clientResponseExtractor) throws HttpClientException;

	HttpResponseEntity<Object> execute(Type responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(Class<? extends T> responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, Object body, MediaType contentType) throws HttpClientException;

	<T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException;

	HttpResponseEntity<Object> get(Type responseType, String url, SSLSocketFactory sslSocketFactory)
			throws HttpClientException;

	<T> HttpResponseEntity<T> get(Class<? extends T> responseType, String url) throws HttpClientException;

	HttpResponseEntity<Object> get(Type responseType, String url) throws HttpClientException;

	<T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException;

	HttpResponseEntity<Object> post(Type responseType, String url, SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException;

	<T> HttpResponseEntity<T> post(Class<? extends T> responseType, String url, Object body, MediaType contentType)
			throws HttpClientException;

	HttpResponseEntity<Object> post(Type responseType, String url, Object body, MediaType contentType)
			throws HttpClientException;
}
