package scw.net.http.client;

import java.lang.reflect.Type;

import javax.net.ssl.SSLSocketFactory;

import scw.net.http.HttpMethod;
import scw.net.http.MediaType;
import scw.net.http.SerializableHttpInputMessage;
import scw.net.http.client.exception.HttpClientException;
import scw.net.message.InputMessage;

public interface HttpClient {
	<T> T execute(
			ClientHttpRequestBuilder builder,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> clientResponseExtractor)
			throws HttpClientException;

	SerializableHttpInputMessage execute(ClientHttpRequestBuilder builder,
			InputMessage inputMessage) throws HttpClientException;

	SerializableHttpInputMessage getSerializableHttpInputMessage(String url, SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerializableHttpInputMessage executeAndGetSerializableHttpInputMessage(
			String url, HttpMethod method, SSLSocketFactory sslSocketFactory,
			Object body, MediaType contentType) throws HttpClientException;

	<T> T get(String url, Class<? extends T> responseType)
			throws HttpClientException;

	<T> T get(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	Object get(String url, Type responseType) throws HttpClientException;

	Object get(String url, Type responseType, SSLSocketFactory sslSocketFactory)
			throws HttpClientException;

	<T> T post(String url, Class<? extends T> responseType, Object body,
			MediaType contentType) throws HttpClientException;

	<T> T post(String url, Class<? extends T> responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException;

	Object post(String url, Type responseType, Object body,
			MediaType contentType) throws HttpClientException;

	Object post(String url, Type responseType,
			SSLSocketFactory sslSocketFactory, Object body,
			MediaType contentType) throws HttpClientException;
}
