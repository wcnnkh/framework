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
		Assert.notNull(clientHttpInputMessageErrorHandler,
				"ClientHttpInputMessageErrorHandler must not be null");
		this.clientHttpInputMessageErrorHandler = clientHttpInputMessageErrorHandler;
	}

	public final String doGet(String url) throws HttpClientException {
		return doGet(url, getDefaultCharsetName());
	}

	public final String doGet(String url, String charsetName)
			throws HttpClientException {
		return doGet(url, charsetName, null);
	}

	public final String doGet(String url, String charsetName,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		SerialzerableClientHttpInputMessage message = execute(url, Method.GET,
				null, new MediaType(MediaType.APPLICATION_FORM_URLENCODED,
						charsetName), null, sslSocketFactory);
		try {
			handleResponse(url, Method.GET, message);
		} catch (IOException e) {
			throw createHttpClientResourceAccessException(e, url, Method.GET);
		}
		return message.convertToString(charsetName);
	}

	public final SerialzerableClientHttpInputMessage doGet(String url,
			HttpHeaders headers) throws HttpClientException {
		return doGet(url, headers, null);
	}

	public final SerialzerableClientHttpInputMessage doGet(String url,
			HttpHeaders headers, SSLSocketFactory sslSocketFactory)
			throws HttpClientException {
		return execute(url, Method.GET, null,
				MediaType.APPLICATION_FORM_URLENCODED, headers,
				sslSocketFactory);
	}

	public final String doPostForJson(String url, String json)
			throws HttpClientException {
		return doPostForJson(url, json, getDefaultCharsetName());
	}

	public final String doPostForJson(String url, String json,
			String charsetName) throws HttpClientException {
		return doPostForJson(url, json, charsetName, null);
	}

	public final String doPostForJson(String url, String json,
			String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException {
		return doPostForJson(url, json, charsetName, null);
	}

	public final String doPostForJson(String url, String body,
			String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return doPost(url, body, charsetName, new MediaType(
				MimeTypeUtils.APPLICATION_JSON, charsetName), httpHeaders,
				sslSocketFactory);
	}

	public final String doPostForFrom(String url, Map<String, ?> parameterMap)
			throws HttpClientException {
		return doPostForFrom(url, parameterMap, getDefaultCharsetName());
	}

	public final String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName) throws HttpClientException {
		return doPostForFrom(url, parameterMap, charsetName, null);
	}

	public final String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName, HttpHeaders httpHeaders)
			throws HttpClientException {
		return doPostForFrom(url, parameterMap, charsetName, httpHeaders, null);
	}

	public final String doPostForFrom(String url, Map<String, ?> parameterMap,
			String charsetName, HttpHeaders httpHeaders,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		String body;
		try {
			body = toFormBody(parameterMap, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw createHttpClientResourceAccessException(e, url, Method.POST);
		}
		return doPost(url, body, charsetName, new MediaType(
				MimeTypeUtils.APPLICATION_X_WWW_FORM_URLENCODED, charsetName),
				httpHeaders, sslSocketFactory);
	}

	protected String toFormBody(Map<String, ?> parameterMap, String charsetName)
			throws UnsupportedEncodingException {
		return HttpUtils.toFormBody(parameterMap, charsetName);
	}

	public String getDefaultCharsetName() {
		return Constants.DEFAULT_CHARSET_NAME;
	}

	public final String doPost(String url, String body, MediaType contentType)
			throws HttpClientException {
		return doPost(url, body, getDefaultCharsetName(), contentType);
	}

	public final String doPost(String url, String body, String charsetName,
			MediaType contentType) throws HttpClientException {
		return doPost(url, body, charsetName, contentType, null);
	}

	public final String doPost(String url, String body, String charsetName,
			MediaType contentType, HttpHeaders headers)
			throws HttpClientException {
		return doPost(url, body, charsetName, contentType, headers, null);
	}

	public final String doPost(String url, String body, String charsetName,
			MediaType contentType, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		SerialzerableClientHttpInputMessage message = doPost(url,
				StringUtils.isEmpty(body) ? null : StringCodecUtils
						.getStringCodec(charsetName).encode(body),
				new MediaType(contentType, charsetName), headers,
				sslSocketFactory);
		try {
			handleResponse(url, Method.POST, message);
		} catch (IOException e) {
			throw createHttpClientResourceAccessException(e, url, Method.POST);
		}
		return message.convertToString(charsetName);
	}

	public final SerialzerableClientHttpInputMessage doPost(String url,
			byte[] body, MediaType contentType) throws HttpClientException {
		return doPost(url, body, contentType, null);
	}

	public final SerialzerableClientHttpInputMessage doPost(String url,
			byte[] body, MediaType contentType, HttpHeaders headers)
			throws HttpClientException {
		return doPost(url, body, contentType, headers, null);
	}

	public final SerialzerableClientHttpInputMessage doPost(String url,
			byte[] body, MediaType contentType, HttpHeaders headers,
			SSLSocketFactory sslSocketFactory) throws HttpClientException {
		return execute(url, Method.POST, body, contentType, headers,
				sslSocketFactory);
	}

	public final SerialzerableClientHttpInputMessage execute(String url,
			Method method, byte[] body, MediaType contentType)
			throws HttpClientException {
		return execute(url, method, body, contentType, null);
	}

	public final SerialzerableClientHttpInputMessage execute(String url,
			Method method, byte[] body, MediaType contentType,
			HttpHeaders headers) throws HttpClientException {
		return execute(url, method, body, contentType, headers, null);
	}

	protected final HttpClientResourceAccessException createHttpClientResourceAccessException(
			IOException ex, String url, Method method) {
		return new HttpClientResourceAccessException("I/O error on "
				+ method.name() + " request for \"" + url + "\": "
				+ ex.getMessage(), ex);
	}

	protected void handleResponse(String url, Method method,
			ClientHttpInputMessage response) throws IOException {
		ClientHttpInputMessageErrorHandler errorHandler = getClientHttpInputMessageErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(method.name() + " request for \"" + url
						+ "\" resulted in " + response.getRawStatusCode()
						+ " (" + response.getStatusText() + ")"
						+ (hasError ? "; invoking error handler" : ""));
			} catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(response);
		}
	}
}
