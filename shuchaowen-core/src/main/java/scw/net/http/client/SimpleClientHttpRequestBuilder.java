package scw.net.http.client;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.net.NetworkUtils;
import scw.net.http.HttpMethod;
import scw.net.http.HttpUtils;

public class SimpleClientHttpRequestBuilder implements ClientHttpRequestBuilder {
	private final URI uri;
	private final HttpMethod method;
	private SSLSocketFactory sslSocketFactory = NetworkUtils.TRUSE_ALL_SSL_SOCKET_FACTORY;
	private int connectTimeout = HttpUtils.DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = HttpUtils.DEFAULT_READ_TIMEOUT;
	private Proxy proxy;

	public SimpleClientHttpRequestBuilder(URI uri, HttpMethod method) {
		Assert.notNull(uri, "'uri' must not be null");
		Assert.notNull(method, "'method' must not be null");
		this.uri = uri;
		this.method = method;
	}

	public SimpleClientHttpRequestBuilder(String url, HttpMethod method) {
		Assert.notNull(url, "'uri' must not be null");
		Assert.notNull(method, "'method' must not be null");
		this.uri = NetworkUtils.toURI(url);
		this.method = method;
	}

	public URI getUri() {
		return uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public ClientHttpRequest builder() throws IOException {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		if (getSslSocketFactory() != null) {
			clientHttpRequestFactory.setSSLSocketFactory(getSslSocketFactory());
		}

		clientHttpRequestFactory.setConnectTimeout(getConnectTimeout());
		clientHttpRequestFactory.setReadTimeout(getReadTimeout());
		clientHttpRequestFactory.setProxy(getProxy());
		return clientHttpRequestFactory.createRequest(getUri(), getMethod());

	}
}
