package scw.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpMethod;
import scw.http.HttpUtils;
import scw.net.NetworkUtils;

public class SimpleClientHttpRequestFactory implements ClientHttpRequestFactory {
	private static final int DEFAULT_CHUNK_SIZE = 4096;

	private Proxy proxy;

	private boolean bufferRequestBody = true;

	private int chunkSize = DEFAULT_CHUNK_SIZE;

	private int connectTimeout = HttpUtils.DEFAULT_CONNECT_TIMEOUT;

	private int readTimeout = HttpUtils.DEFAULT_READ_TIMEOUT;

	private boolean outputStreaming = true;

	private SSLSocketFactory sslSocketFactory = NetworkUtils.TRUSE_ALL_SSL_SOCKET_FACTORY;

	/**
	 * Set the {@link Proxy} to use for this request factory.
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}

	/**
	 * Indicate whether this request factory should buffer the
	 * {@linkplain ClientHttpRequest#getBody() request body} internally.
	 * <p>
	 * Default is {@code true}. When sending large amounts of data via POST or
	 * PUT, it is recommended to change this property to {@code false}, so as
	 * not to run out of memory. This will result in a {@link ClientHttpRequest}
	 * that either streams directly to the underlying {@link HttpURLConnection}
	 * (if the
	 * {@link scw.net.http.springframework.http.HttpHeaders#getContentLength()
	 * Content-Length} is known in advance), or that will use "Chunked transfer
	 * encoding" (if the {@code Content-Length} is not known in advance).
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
	 * {@link scw.net.http.springframework.http.HttpHeaders#getContentLength()
	 * Content-Length} is not known in advance.
	 * 
	 * @see #setBufferRequestBody(boolean)
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	/**
	 * Set the underlying URLConnection's connect timeout (in milliseconds). A
	 * timeout value of 0 specifies an infinite timeout.
	 * <p>
	 * Default is the system's default timeout.
	 * 
	 * @see URLConnection#setConnectTimeout(int)
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * Set the underlying URLConnection's read timeout (in milliseconds). A
	 * timeout value of 0 specifies an infinite timeout.
	 * <p>
	 * Default is the system's default timeout.
	 * 
	 * @see URLConnection#setReadTimeout(int)
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * Set if the underlying URLConnection can be set to 'output streaming'
	 * mode. Default is {@code true}.
	 * <p>
	 * When output streaming is enabled, authentication and redirection cannot
	 * be handled automatically. If output streaming is disabled, the
	 * {@link HttpURLConnection#setFixedLengthStreamingMode} and
	 * {@link HttpURLConnection#setChunkedStreamingMode} methods of the
	 * underlying connection will never be called.
	 * 
	 * @param outputStreaming
	 *            if output streaming is enabled
	 */
	public void setOutputStreaming(boolean outputStreaming) {
		this.outputStreaming = outputStreaming;
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		HttpURLConnection connection = openConnection(uri.toURL(), this.proxy);
		prepareConnection(connection, httpMethod.name());

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
		if (this.connectTimeout >= 0) {
			connection.setConnectTimeout(this.connectTimeout);
		}
		if (this.readTimeout >= 0) {
			connection.setReadTimeout(this.readTimeout);
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

		if (sslSocketFactory != null) {
			if (connection instanceof HttpsURLConnection) {
				((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
			}
		}
	}

}
