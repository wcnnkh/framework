package scw.net.http.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.core.Constants;
import scw.core.string.StringCodecUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.MimeTypeUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpUtils;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.http.client.exception.HttpClientException;
import scw.net.http.client.exception.HttpClientResourceAccessException;

public abstract class AbstractHttpClient implements HttpClient {
	protected Logger logger = LoggerUtils.getLogger(getClass());
	private ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler = new DefaultClientHttpInputMessageErrorHandler();

	public ClientHttpInputMessageErrorHandler getClientHttpInputMessageErrorHandler() {
		return clientHttpInputMessageErrorHandler;
	}

	public void setClientHttpInputMessageErrorHandler(
			ClientHttpInputMessageErrorHandler clientHttpInputMessageErrorHandler) {
		Assert.notNull(clientHttpInputMessageErrorHandler, "ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpInputMessageErrorHandler = clientHttpInputMessageErrorHandler;
	}

	public final String get(String url) throws HttpClientException {
		return get(url, getDefaultCharsetName());
	}

	public final String get(String url, String charsetName) throws HttpClientException {
		return get(url, charsetName, null);
	}

	public final String get(String url, String charsetName, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		SerialzerableClientHttpInputMessage message = execute(url, Method.GET, null,
				new MediaType(MediaType.APPLICATION_FORM_URLENCODED, charsetName), null, sslSocketFactory);
		try {
			handleResponse(url, Method.GET, message);
		} catch (IOException e) {
			throw createHttpClientResourceAccessException(e, url, Method.GET);
		}
		return message.convertToString(charsetName);
	}

	public SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url) throws HttpClientException {
		return getToSerialzerableInputMessage(url, null);
	}

	public final SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url, HttpHeaders httpHeaders)
			throws HttpClientException {
		return getToSerialzerableInputMessage(url, httpHeaders, null);
	}

	public final SerialzerableClientHttpInputMessage getToSerialzerableInputMessage(String url, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return execute(url, Method.GET, null, MediaType.APPLICATION_FORM_URLENCODED, httpHeaders, sslSocketFactory);
	}

	public final String postForJson(String url, String json) throws HttpClientException {
		return postForJson(url, json, getDefaultCharsetName());
	}

	public final String postForJson(String url, String json, String charsetName) throws HttpClientException {
		return postForJson(url, json, charsetName, null);
	}

	public final String postForJson(String url, String json, String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException {
		return postForJson(url, json, charsetName, null);
	}

	public final String postForJson(String url, String body, String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return post(url, body, charsetName, new MediaType(MimeTypeUtils.APPLICATION_JSON, charsetName), httpHeaders,
				sslSocketFactory);
	}

	public final String postForFrom(String url, Map<String, ?> parameterMap) throws HttpClientException {
		return postForFrom(url, parameterMap, getDefaultCharsetName());
	}

	public final String postForFrom(String url, Map<String, ?> parameterMap, String charsetName)
			throws HttpClientException {
		return postForFrom(url, parameterMap, charsetName, null);
	}

	public final String postForFrom(String url, Map<String, ?> parameterMap, String charsetName,
			HttpHeaders httpHeaders) throws HttpClientException {
		return postForFrom(url, parameterMap, charsetName, httpHeaders, null);
	}

	public final String postForFrom(String url, Map<String, ?> parameterMap, String charsetName,
			HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException {
		String body;
		try {
			body = toFormBody(parameterMap, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw createHttpClientResourceAccessException(e, url, Method.POST);
		}
		return post(url, body, charsetName, new MediaType(MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName),
				httpHeaders, sslSocketFactory);
	}

	protected String toFormBody(Map<String, ?> parameterMap, String charsetName) throws UnsupportedEncodingException {
		return HttpUtils.toFormBody(parameterMap, charsetName);
	}

	public final String postForXml(String url, String xml) throws HttpClientException {
		return postForXml(url, xml, getDefaultCharsetName());
	}

	public final String postForXml(String url, String xml, String charsetName) throws HttpClientException {
		return postForXml(url, xml, charsetName, null);
	}

	public final String postForXml(String url, String xml, String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException {
		return postForXml(url, xml, charsetName, httpHeaders, null);
	}

	public final String postForXml(String url, String xml, String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return post(url, xml, charsetName, new MediaType(MediaType.APPLICATION_XML, charsetName), httpHeaders,
				sslSocketFactory);
	}

	public String getDefaultCharsetName() {
		return Constants.DEFAULT_CHARSET_NAME;
	}

	public final String post(String url, String body, MediaType contentType) throws HttpClientException {
		return post(url, body, getDefaultCharsetName(), contentType);
	}

	public final String post(String url, String body, String charsetName, MediaType contentType)
			throws HttpClientException {
		return post(url, body, charsetName, contentType, null);
	}

	public final String post(String url, String body, String charsetName, MediaType contentType,
			HttpHeaders httpHeaders) throws HttpClientException {
		return post(url, body, charsetName, contentType, httpHeaders, null);
	}

	public final String post(String url, String body, String charsetName, MediaType contentType,
			HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory) throws HttpClientException {
		SerialzerableClientHttpInputMessage message = postToSerialzerableInputMessage(url,
				StringUtils.isEmpty(body) ? null : StringCodecUtils.getStringCodec(charsetName).encode(body),
				new MediaType(contentType, charsetName), httpHeaders, sslSocketFactory);
		try {
			handleResponse(url, Method.POST, message);
		} catch (IOException e) {
			throw createHttpClientResourceAccessException(e, url, Method.POST);
		}
		return message.convertToString(charsetName);
	}

	public final SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body,
			MediaType contentType) throws HttpClientException {
		return postToSerialzerableInputMessage(url, body, contentType, null);
	}

	public final SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body,
			MediaType contentType, HttpHeaders httpHeaders) throws HttpClientException {
		return postToSerialzerableInputMessage(url, body, contentType, httpHeaders, null);
	}

	public final SerialzerableClientHttpInputMessage postToSerialzerableInputMessage(String url, byte[] body,
			MediaType contentType, HttpHeaders httpHeaders, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(url, Method.POST, body, contentType, httpHeaders, sslSocketFactory);
	}

	public final SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body,
			MediaType contentType) throws HttpClientException {
		return execute(url, method, body, contentType, null);
	}

	public final SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body,
			MediaType contentType, HttpHeaders httpHeaders) throws HttpClientException {
		return execute(url, method, body, contentType, httpHeaders, null);
	}

	protected final HttpClientResourceAccessException createHttpClientResourceAccessException(IOException ex,
			String url, Method method) {
		return new HttpClientResourceAccessException(
				"I/O error on " + method.name() + " request for \"" + url + "\": " + ex.getMessage(), ex);
	}

	protected void handleResponse(String url, Method method, ClientHttpInputMessage response) throws IOException {
		ClientHttpInputMessageErrorHandler errorHandler = getClientHttpInputMessageErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(method.name() + " request for \"" + url + "\" resulted in " + response.getRawStatusCode()
						+ " (" + response.getStatusText() + ")" + (hasError ? "; invoking error handler" : ""));
			} catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}
}
