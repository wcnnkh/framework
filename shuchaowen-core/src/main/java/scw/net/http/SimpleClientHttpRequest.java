package scw.net.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import scw.core.utils.StringUtils;
import scw.io.UnsafeByteArrayOutputStream;
import scw.lang.NotSupportException;
import scw.net.AbstractUrlRequestCallback;
import scw.net.NetworkUtils;
import scw.net.RequestException;
import scw.net.URLConnectionResponseCallback;
import scw.net.URLRequestCallback;
import scw.net.message.CacheURLConnectionInputMessage;
import scw.net.mime.MimeType;
import scw.net.mime.MimeTypeUtils;

public class SimpleClientHttpRequest extends AbstractUrlRequestCallback implements URLRequestCallback, ClientHttpRequest {
	private Method method;
	private Map<String, String> requestProperties;
	private String requestUrl;
	private SSLSocketFactory sslSocketFactory;
	private UnsafeByteArrayOutputStream outputStream;

	public SimpleClientHttpRequest(Method method, String requestUrl) {
		this.method = method;
		this.requestUrl = requestUrl;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public OutputStream getBody() {
		if (outputStream == null) {
			outputStream = new UnsafeByteArrayOutputStream();
		}
		return outputStream;
	}

	@Override
	public void request(URLConnection urlConnection) throws Throwable {
		HttpURLConnection http = (HttpURLConnection) urlConnection;
		if (http instanceof HttpsURLConnection) {
			SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
			if (sslSocketFactory == null) {
				sslSocketFactory = NetworkUtils.TRUSE_ALL_SSL_SOCKET_FACTORY;
			}

			if (sslSocketFactory != null) {
				((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
			}
		}

		http.setRequestMethod(method.name());

		urlConnection.setConnectTimeout(10000);
		urlConnection.setReadTimeout(10000);
		if (method != Method.GET) {
			urlConnection.setDoOutput(true);
		}
		urlConnection.setDoInput(true);

		if (requestProperties != null) {
			for (Entry<String, String> entry : requestProperties.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		super.request(urlConnection);
	}

	@Override
	protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
		if (outputStream != null) {
			outputStream.writeTo(os);
		}
	}

	public void setRequestProperties(String key, Object value) {
		if (value == null) {
			return;
		}

		if (requestProperties == null) {
			requestProperties = new HashMap<String, String>();
		}
		requestProperties.put(key, value.toString());
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public void setContentType(String contentType) {
		setRequestProperties("Content-Type", contentType);
	}

	public void setRequestProperties(Map<String, String> requestProperties) {
		this.requestProperties = requestProperties;
	}

	public void setContentType(MimeType mimeType) {
		setRequestProperties("Content-Type", mimeType.toString());
	}

	public URL getURL() {
		try {
			return new URL(getRequestUrl());
		} catch (MalformedURLException e) {
			throw new RequestException(getRequestUrl(), e);
		}
	}

	public final SSLSocketFactory getSSLSocketFactory() {
		return sslSocketFactory;
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	public Proxy getProxy() {
		return null;
	}

	public SimpleClientHttpResponse execute() {
		return NetworkUtils.execute(this, new URLConnectionResponseCallback<SimpleClientHttpResponse>() {

			public SimpleClientHttpResponse response(URLConnection urlConnection) throws Throwable {
				if (urlConnection instanceof HttpURLConnection) {
					return new SimpleClientHttpResponse((HttpURLConnection) urlConnection);
				}
				throw new NotSupportException(urlConnection.toString());
			}
		});
	}

	private static class SimpleClientHttpResponse extends CacheURLConnectionInputMessage implements ClientHttpResponse {
		private static final long serialVersionUID = 1L;
		private final int responseCoce;
		private final String responseMessage;

		public SimpleClientHttpResponse(HttpURLConnection httpURLConnection) throws IOException {
			super(httpURLConnection);
			this.responseCoce = httpURLConnection.getResponseCode();
			this.responseMessage = httpURLConnection.getResponseMessage();
		}

		public void close() throws IOException {
			// IGNORE
		}

		public int getResponseCode() {
			return responseCoce;
		}

		public String getResponseMessage() {
			return responseMessage;
		}

	}

	public void setContentLength(long contentLength) {
		requestProperties.put("content-length", contentLength + "");
	}

	public MimeType getContentType() {
		String contentType = requestProperties.get("Content-Type");
		return StringUtils.hasLength(contentType) ? MimeTypeUtils.parseMimeType(contentType) : null;
	}

	public long getContentLength() {
		String length = requestProperties.get("content-length");
		return StringUtils.hasLength(length) ? StringUtils.parseLong(length) : -1;
	}
}
