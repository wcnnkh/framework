package scw.net.http.client;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import scw.net.http.HttpHeaders;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.http.client.exception.HttpClientException;

public interface HttpClient {
	String getDefaultCharsetName();

	String doGet(String url) throws HttpClientException;

	String doGet(String url, String charsetName) throws HttpClientException;

	String doGet(String url, String charsetName,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage doGet(String url, HttpHeaders headers)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage doGet(String url, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String doPostForJson(String url, String json) throws HttpClientException;

	String doPostForJson(String url, String json, String charsetName)
			throws HttpClientException;

	String doPostForJson(String url, String json, String charsetName,
			HttpHeaders httpHeaders) throws HttpClientException;

	String doPostForJson(String url, String json, String charsetName,
			HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory)
			throws HttpClientException;

	String doPostForFrom(String url, Map<String, ?> parameterMap)
			throws HttpClientException;

	String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName) throws HttpClientException;

	String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException;

	String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String doPost(String url, String body, MediaType contentType)
			throws HttpClientException;

	String doPost(String url, String body, String charsetName,
			MediaType contentType) throws HttpClientException;

	String doPost(String url, String body, String charsetName,
			MediaType contentType, HttpHeaders headers)
			throws HttpClientException;

	String doPost(String url, String body, String charsetName,
			MediaType contentType, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage doPost(String url, byte[] body,
			MediaType contentType) throws HttpClientException;

	SerialzerableClientHttpInputMessage doPost(String url, byte[] body,
			MediaType contentType, HttpHeaders headers)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage doPost(String url, byte[] body,
			MediaType contentType, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method,
			byte[] body, MediaType contentType) throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method,
			byte[] body, MediaType contentType, HttpHeaders headers)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method,
			byte[] body, MediaType contentType, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;
}
