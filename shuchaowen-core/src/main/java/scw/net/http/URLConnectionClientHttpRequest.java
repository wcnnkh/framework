package scw.net.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.URLConnectionClientRequest;
import scw.net.message.CacheURLConnectionInputMessage;

public class URLConnectionClientHttpRequest extends URLConnectionClientRequest implements ClientHttpRequest {
	private static Logger logger = LoggerUtils.getLogger(URLConnectionClientHttpRequest.class);
	private Proxy proxy;
	private HttpURLConnection httpURLConnection;

	public URLConnectionClientHttpRequest(URL url, Proxy proxy, Method method) throws IOException {
		this.httpURLConnection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
		this.proxy = proxy;
		httpURLConnection.setRequestMethod(method.name());
		setConnectTimeout(getConnectTimeout());
		setReadTimeout(getReadTimeout());
		if (method != Method.GET) {
			httpURLConnection.setDoOutput(true);
		}

		if (method != Method.HEAD) {
			httpURLConnection.setDoInput(true);
		}
	}

	public final Proxy getProxy() {
		return proxy;
	}

	@Override
	public HttpURLConnection getUrlConnection() {
		return httpURLConnection;
	}

	public Method getMethod() {
		return Method.valueOf(getUrlConnection().getRequestMethod());
	}

	public SSLSocketFactory getSSLSocketFactory() {
		if (httpURLConnection instanceof HttpsURLConnection) {
			return ((HttpsURLConnection) httpURLConnection).getSSLSocketFactory();
		}
		return null;
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		if (!(httpURLConnection instanceof HttpsURLConnection)) {
			if (logger.isWarnEnabled()) {
				logger.warn("无法设置SSLSocketFactory, 这不是一个https请求:{}", httpURLConnection.getURL());
			}
			return;
		}

		((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslSocketFactory);
	}

	public CacheHttpURLConnectionClientResponse execute() throws IOException {
		try {
			getUrlConnection().connect();
			return new CacheHttpURLConnectionClientResponse(getUrlConnection());
		} finally {
			getUrlConnection().disconnect();
		}
	};

	public static class CacheHttpURLConnectionClientResponse extends CacheURLConnectionInputMessage
			implements ClientHttpResponse {
		private static final long serialVersionUID = 1L;
		private String responseMessage;
		private int responseCode;

		public CacheHttpURLConnectionClientResponse(HttpURLConnection httpURLConnection) throws IOException {
			super(httpURLConnection);
			this.responseMessage = httpURLConnection.getResponseMessage();
			this.responseCode = httpURLConnection.getResponseCode();
		}

		public void close() throws IOException {
			// IGNORE
		}

		public int getResponseCode() {
			return responseCode;
		}

		public String getResponseMessage() {
			return responseMessage;
		}

	}
}
