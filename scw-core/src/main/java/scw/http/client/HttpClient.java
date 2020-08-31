package scw.http.client;

import java.lang.reflect.Type;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpEntity;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.MediaType;
import scw.http.client.exception.HttpClientException;
import scw.io.support.TemporaryFile;

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
	 * @param uri
	 * @param httpHeaders
	 * @param sslSocketFactory
	 * @param supportedRedirect 是否支持重定向
	 * @return
	 * @throws HttpClientException
	 */
	HttpResponseEntity<TemporaryFile> download(String uri, HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory, boolean supportedRedirect) throws HttpClientException;
	
	/**
	 * 下载文件
	 * @param uri
	 * @param httpHeaders
	 * @param sslSocketFactory
	 * @param supportedRedirect 是否支持重定向
	 * @return
	 * @throws HttpClientException
	 */
	HttpResponseEntity<TemporaryFile> download(URI uri, HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory, boolean supportedRedirect) throws HttpClientException;
	
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
