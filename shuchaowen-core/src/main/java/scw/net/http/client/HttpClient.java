package scw.net.http.client;

import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import scw.net.http.HttpHeaders;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.http.client.exception.HttpClientException;

public interface HttpClient {
	String getDefaultCharsetName();

	String get(String url) throws HttpClientException;

	String get(String url, String charsetName) throws HttpClientException;

	String get(String url, String charsetName, SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url) throws HttpClientException;

	SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url, HttpHeaders httpHeaders)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String postForJson(String url, String json) throws HttpClientException;

	String postForJson(String url, String json, String charsetName) throws HttpClientException;

	String postForJson(String url, String json, String charsetName, HttpHeaders httpHeaders) throws HttpClientException;

	String postForJson(String url, String json, String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String postForFrom(String url, Map<String, ?> parameterMap) throws HttpClientException;

	String postForFrom(String url, Map<String, ?> parameterMap, String charsetName) throws HttpClientException;

	String postForFrom(String url, Map<String, ?> parameterMap, String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException;

	String postForFrom(String url, Map<String, ?> parameterMap, String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String postForXml(String url, String xml) throws HttpClientException;

	String postForXml(String url, String xml, String charsetName) throws HttpClientException;

	String postForXml(String url, String xml, String charsetName, HttpHeaders httpHeaders) throws HttpClientException;

	String postForXml(String url, String xml, String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	String post(String url, String body, MediaType contentType) throws HttpClientException;

	String post(String url, String body, String charsetName, MediaType contentType) throws HttpClientException;

	String post(String url, String body, String charsetName, MediaType contentType, HttpHeaders httpHeaders)
			throws HttpClientException;

	String post(String url, String body, String charsetName, MediaType contentType, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body, MediaType contentType)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body, MediaType contentType,
			HttpHeaders httpHeaders) throws HttpClientException;

	SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body, MediaType contentType,
			HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body, MediaType contentType)
			throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body, MediaType contentType,
			HttpHeaders httpHeaders) throws HttpClientException;

	SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body, MediaType contentType,
			HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException;
}
