package scw.http.client;

import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.http.SerializableHttpInputMessage;
import scw.http.client.exception.HttpClientException;
import scw.net.message.InputMessage;

/**
 * 一个http客户端
 * @author shuchaowen
 *
 */
public interface HttpClient {
	/**
	 * @param builder
	 * @param requestCallback
	 * @param clientResponseExtractor
	 * @return
	 * @throws HttpClientException
	 */
	<T> T execute(
			ClientHttpRequestBuilder builder,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException;

	/**
	 * @param builder
	 * @param inputMessage
	 * @return
	 * @throws HttpClientException
	 */
	SerializableHttpInputMessage execute(ClientHttpRequestBuilder builder,
			InputMessage inputMessage) throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url
	 * @param sslSocketFactory
	 * @return
	 * @throws HttpClientException
	 */
	SerializableHttpInputMessage getSerializableHttpInputMessage(String url, SSLSocketFactory sslSocketFactory) throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url
	 * @param method
	 * @param sslSocketFactory
	 * @param body
	 * @param contentType
	 * @return
	 * @throws HttpClientException
	 */
	SerializableHttpInputMessage executeAndGetSerializableHttpInputMessage(
			String url, HttpMethod method, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url 请求地址
	 * @param responseType 返回类型
	 * @return
	 * @throws HttpClientException
	 */
	<T> T get(String url, Class<? extends T> responseType)
			throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url
	 * @param responseType
	 * @param sslSocketFactory
	 * @return
	 * @throws HttpClientException
	 */
	<T> T get(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url
	 * @param responseType
	 * @return
	 * @throws HttpClientException
	 */
	Object get(String url, Type responseType) throws HttpClientException;

	/**
	 * 发送一个GET请求
	 * @param url
	 * @param responseType
	 * @param sslSocketFactory
	 * @return
	 * @throws HttpClientException
	 */
	Object get(String url, Type responseType, SSLSocketFactory sslSocketFactory)
			throws HttpClientException;

	/**
	 * 发送一个POST请求
	 * @param url
	 * @param responseType
	 * @param body
	 * @param contentType
	 * @return
	 * @throws HttpClientException
	 */
	<T> T post(String url, Class<? extends T> responseType, Object body,
			MediaType contentType) throws HttpClientException;

	/**
	 * 发送一个POST请求
	 * @param url
	 * @param responseType
	 * @param sslSocketFactory
	 * @param body
	 * @param contentType
	 * @return
	 * @throws HttpClientException
	 */
	<T> T post(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException;

	/**
	 * 发送一个POST请求
	 * @param url
	 * @param responseType
	 * @param body
	 * @param contentType
	 * @return
	 * @throws HttpClientException
	 */
	Object post(String url, Type responseType, Object body,
			MediaType contentType) throws HttpClientException;

	/**
	 * 发送一个POST请求
	 * @param url
	 * @param responseType
	 * @param sslSocketFactory
	 * @param body
	 * @param contentType
	 * @return
	 * @throws HttpClientException
	 */
	Object post(String url, Type responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException;
}
