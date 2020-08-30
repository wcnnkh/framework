package scw.http.client;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpEntity;
import scw.http.HttpHeaders;
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
	
	<T> HttpResponseEntity<T> execute(String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity, ClientHttpResponseExtractor<T> clientResponseExtractor) throws HttpClientException;
	
	<T> HttpResponseEntity<T> execute(URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity, ClientHttpResponseExtractor<T> clientResponseExtractor) throws HttpClientException;
	
	/**
	 * 下载文件
	 * @param file
	 * @param url
	 * @param httpHeaders
	 * @param sslSocketFactory
	 * @return 如果返回的 body(file) 为空，那么说明下载失败
	 * @throws HttpClientException
	 */
	HttpResponseEntity<File> download(File file, String url, HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException;
	
	HttpResponseEntity<File> download(File file, URI uri, HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException;
	
	HttpResponseEntity<Object> execute(Type responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException;

	HttpResponseEntity<Object> execute(Type responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException;
	
	<T> HttpResponseEntity<T> execute(Class<? extends T> responseType, String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException;

	<T> HttpResponseEntity<T> execute(Class<? extends T> responseType, URI uri, HttpMethod method,
			SSLSocketFactory sslSocketFactory, HttpEntity<?> httpEntity) throws HttpClientException;
	
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
