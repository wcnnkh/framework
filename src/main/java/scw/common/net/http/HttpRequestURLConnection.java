package scw.common.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import scw.common.io.decoder.StringDecoder;
import scw.common.net.RequestURLConnection;

public class HttpRequestURLConnection extends RequestURLConnection implements HttpRequest{
	private final HttpURLConnection httpURLConnection;
	
	public HttpRequestURLConnection(String url) throws MalformedURLException, IOException{
		this((HttpURLConnection)new URL(url).openConnection());
		setConnectTimeout(10000);
		setReadTimeout(10000);
	}
	
	public HttpRequestURLConnection(String url, Proxy proxy) throws MalformedURLException, IOException{
		this((HttpURLConnection)new URL(url).openConnection(proxy));
		setConnectTimeout(10000);
		setReadTimeout(10000);
	}
	
	public HttpRequestURLConnection(HttpURLConnection httpURLConnection) {
		super(httpURLConnection);
		this.httpURLConnection = httpURLConnection;
	}

	public void setFixedLengthStreamingMode(int contentLength) {
		httpURLConnection.setFixedLengthStreamingMode(contentLength);
	}

	public void setFixedLengthStreamingMode(long contentLength) {
		httpURLConnection.setFixedLengthStreamingMode(contentLength);
	}

	public void setChunkedStreamingMode(int chunklen) {
		httpURLConnection.setChunkedStreamingMode(chunklen);
	}

	public void setInstanceFollowRedirects(boolean followRedirects) {
		httpURLConnection.setInstanceFollowRedirects(followRedirects);
	}

	public boolean getInstanceFollowRedirects() {
		return httpURLConnection.getInstanceFollowRedirects();
	}

	public void setRequestMethod(String method) throws ProtocolException {
		httpURLConnection.setRequestMethod(method);
	}

	public String getRequestMethod() {
		return httpURLConnection.getRequestMethod();
	}

	public int getResponseCode() throws IOException {
		return httpURLConnection.getResponseCode();
	}

	public String getResponseMessage() throws IOException {
		return httpURLConnection.getResponseMessage();
	}

	public void disconnect() {
		httpURLConnection.disconnect();
	}

	public boolean usingProxy() {
		return httpURLConnection.usingProxy();
	}

	public InputStream getErrorStream() {
		return httpURLConnection.getErrorStream();
	}

	public String getResponseBody(Charset charset) throws IOException {
		return new StringDecoder(charset).decode(getInputStream());
	}

	public String getRequestContentType() {
		return getRequestProperty("Content-Type");
	}

	public void setRequestContentType(String contentType) {
		setRequestProperty("Content-Type", contentType);
	}
}
