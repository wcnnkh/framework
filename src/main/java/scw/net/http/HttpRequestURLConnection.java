package scw.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import scw.common.utils.IOUtils;
import scw.net.RequestURLConnection;

public class HttpRequestURLConnection extends RequestURLConnection implements HttpRequest {

	public HttpRequestURLConnection(String url) throws MalformedURLException, IOException {
		this((HttpURLConnection) new URL(url).openConnection());
	}

	public HttpRequestURLConnection(String url, Proxy proxy) throws MalformedURLException, IOException {
		this((HttpURLConnection) new URL(url).openConnection(proxy));
	}

	public HttpRequestURLConnection(HttpURLConnection httpURLConnection) {
		super(httpURLConnection);
		init();
	}

	protected void init() {
		setConnectTimeout(10000);
		setReadTimeout(10000);
	}

	public void setFixedLengthStreamingMode(int contentLength) {
		getHttpURLConnection().setFixedLengthStreamingMode(contentLength);
	}

	public void setFixedLengthStreamingMode(long contentLength) {
		getHttpURLConnection().setFixedLengthStreamingMode(contentLength);
	}

	public void setChunkedStreamingMode(int chunklen) {
		getHttpURLConnection().setChunkedStreamingMode(chunklen);
	}

	public void setInstanceFollowRedirects(boolean followRedirects) {
		getHttpURLConnection().setInstanceFollowRedirects(followRedirects);
	}

	public boolean getInstanceFollowRedirects() {
		return getHttpURLConnection().getInstanceFollowRedirects();
	}

	public void setRequestMethod(String method) throws ProtocolException {
		getHttpURLConnection().setRequestMethod(method);
	}

	public String getRequestMethod() {
		return getHttpURLConnection().getRequestMethod();
	}

	public int getResponseCode() throws IOException {
		return getHttpURLConnection().getResponseCode();
	}

	public String getResponseMessage() throws IOException {
		return getHttpURLConnection().getResponseMessage();
	}

	public void disconnect() {
		getHttpURLConnection().disconnect();
	}

	public boolean usingProxy() {
		return getHttpURLConnection().usingProxy();
	}

	public InputStream getErrorStream() {
		return getHttpURLConnection().getErrorStream();
	}

	public String getResponseBody(Charset charset) throws IOException {
		InputStreamReader isr = new InputStreamReader(getInputStream(), charset);
		return IOUtils.read(isr, 256, 0);
	}

	public String getRequestContentType() {
		return getRequestProperty("Content-Type");
	}

	public void setRequestContentType(String contentType) {
		setRequestProperty("Content-Type", contentType);
	}

	public HttpURLConnection getHttpURLConnection() {
		return (HttpURLConnection) getURLConnection();
	}
}
