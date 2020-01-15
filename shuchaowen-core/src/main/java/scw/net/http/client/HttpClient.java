package scw.net.http.client;

import javax.net.ssl.SSLSocketFactory;

import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.http.client.exception.HttpClientException;
import scw.net.message.BufferingResponseMessage;
import scw.net.message.Headers;

public interface HttpClient {
	BufferingResponseMessage execute(String url, Method method, byte[] body, MediaType contentType, Headers headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;
}
