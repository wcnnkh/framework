package io.basc.framework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import io.basc.framework.env.Sys;
import io.basc.framework.http.client.accessor.HttpClientConfigAccessor;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.ssl.SSLContexts;
import io.basc.framework.net.ssl.TrustAllManager;
import io.basc.framework.util.StringUtils;

public class SimpleClientHttpRequestFactory extends HttpClientConfigAccessor implements ClientHttpRequestFactory {
	private static Logger logger = LoggerFactory.getLogger(SimpleClientHttpRequestFactory.class);

	/**
	 * 一个信任所有的ssl socket factory <br/>
	 * 注意:在初始化失败后可能为空
	 */
	public static final SSLSocketFactory TRUSE_ALL_SSL_SOCKET_FACTORY;

	static {
		// 创建一个信任所有的
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new TrustAllManager();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = null;
		try {
			sc = javax.net.ssl.SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		TRUSE_ALL_SSL_SOCKET_FACTORY = sc == null ? null : sc.getSocketFactory();
	}

	private static final int DEFAULT_CHUNK_SIZE = 4096;

	private Proxy proxy;

	private boolean bufferRequestBody = true;

	private int chunkSize = DEFAULT_CHUNK_SIZE;

	private boolean outputStreaming = true;

	private SSLSocketFactory sslSocketFactory;

	/**
	 * Set the {@link Proxy} to use for this request factory.
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	public boolean setSSLSocketFactory(String certTrustFile, String storePassword, String keyPassword) {
		if (StringUtils.isEmpty(certTrustFile)) {
			return false;
		}

		Resource resource = Sys.env.getResource(certTrustFile);
		if (!resource.exists()) {
			logger.warn("not found certTrustFile: {}", certTrustFile);
			return false;
		}

		InputStream is = ResourceUtils.getInputStream(resource);
		try {
			this.sslSocketFactory = SSLContexts.custom()
					.loadKeyMaterial(is, storePassword.toCharArray(), keyPassword.toCharArray()).build()
					.getSocketFactory();
		} catch (Exception e) {
			logger.error(e, "certTrustFile [{}], storePassword [{}], keyPassword [{}]", certTrustFile, storePassword,
					keyPassword);
			return false;
		}
		return true;
	}

	/**
	 * Indicate whether this request factory should buffer the
	 * {@linkplain ClientHttpRequest#getBody() request body} internally.
	 * 
	 * @see #setChunkSize(int)
	 * @see HttpURLConnection#setFixedLengthStreamingMode(int)
	 */
	public void setBufferRequestBody(boolean bufferRequestBody) {
		this.bufferRequestBody = bufferRequestBody;
	}

	/**
	 * Set the number of bytes to write in each chunk when not buffering request
	 * bodies locally.
	 * <p>
	 * Note that this parameter is only used when
	 * {@link #setBufferRequestBody(boolean) bufferRequestBody} is set to
	 * {@code false}, and the
	 * Content-Length} is not known in advance.
	 * 
	 * @see #setBufferRequestBody(boolean)
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * Set if the underlying URLConnection can be set to 'output streaming' mode.
	 * Default is {@code true}.
	 * <p>
	 * When output streaming is enabled, authentication and redirection cannot be
	 * handled automatically. If output streaming is disabled, the
	 * {@link HttpURLConnection#setFixedLengthStreamingMode} and
	 * {@link HttpURLConnection#setChunkedStreamingMode} methods of the underlying
	 * connection will never be called.
	 * 
	 * @param outputStreaming if output streaming is enabled
	 */
	public void setOutputStreaming(boolean outputStreaming) {
		this.outputStreaming = outputStreaming;
	}

	public ClientHttpRequest createRequest(URI uri, String httpMethod) throws IOException {
		HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
		prepareConnection(connection, httpMethod);

		if (this.bufferRequestBody) {
			return new SimpleBufferingClientHttpRequest(connection, this.outputStreaming);
		} else {
			return new SimpleStreamingClientHttpRequest(connection, this.chunkSize, this.outputStreaming);
		}
	}

	protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
		URLConnection urlConnection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
		if (!HttpURLConnection.class.isInstance(urlConnection)) {
			throw new IllegalStateException("HttpURLConnection required for [" + url + "] but got: " + urlConnection);
		}
		return (HttpURLConnection) urlConnection;
	}

	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		Integer connectTimeout = getConnectTimeout();
		if (connectTimeout != null && connectTimeout >= 0) {
			connection.setConnectTimeout(connectTimeout);
		}

		Integer readTimeout = getReadTimeout();
		if (readTimeout != null && readTimeout >= 0) {
			connection.setReadTimeout(readTimeout);
		}

		connection.setDoInput(true);

		if ("GET".equals(httpMethod)) {
			connection.setInstanceFollowRedirects(true);
		} else {
			connection.setInstanceFollowRedirects(false);
		}

		if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)
				|| "DELETE".equals(httpMethod)) {
			connection.setDoOutput(true);
		} else {
			connection.setDoOutput(false);
		}

		connection.setRequestMethod(httpMethod);

		if (connection instanceof HttpsURLConnection) {
			SSLSocketFactory factory = getSslSocketFactory();
			if (factory == null) {
				factory = TRUSE_ALL_SSL_SOCKET_FACTORY;
			}
			((HttpsURLConnection) connection).setSSLSocketFactory(factory);
		}
	}

}
