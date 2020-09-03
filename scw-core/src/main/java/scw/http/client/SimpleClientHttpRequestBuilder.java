package scw.http.client;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.http.HttpMethod;
import scw.http.client.accessor.HttpClientConfigAccessor;
import scw.net.InetUtils;

public class SimpleClientHttpRequestBuilder extends HttpClientConfigAccessor implements ClientHttpRequestBuilder {
	private final URI uri;
	private final HttpMethod method;
	private SSLSocketFactory sslSocketFactory = InetUtils.TRUSE_ALL_SSL_SOCKET_FACTORY;
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
		this.uri = InetUtils.toURI(url);
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

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public ClientHttpRequest build() throws IOException {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		if (getSslSocketFactory() != null) {
			clientHttpRequestFactory.setSSLSocketFactory(getSslSocketFactory());
		}

		clientHttpRequestFactory.setProxy(getProxy());
		clientHttpRequestFactory.setConfig(this);
		return clientHttpRequestFactory.createRequest(getUri(), getMethod());

	}
}
