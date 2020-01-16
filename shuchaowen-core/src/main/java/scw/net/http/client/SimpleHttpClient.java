package scw.net.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLSocketFactory;

import scw.core.Assert;
import scw.io.IOUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpUtils;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.http.client.exception.HttpClientException;

public class SimpleHttpClient extends AbstractHttpClient {
	private int connectTimeout = HttpUtils.DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = HttpUtils.DEFAULT_READ_TIMEOUT;

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

	public SerialzerableClientHttpInputMessage execute(String url, Method method, byte[] body, MediaType contentType,
			HttpHeaders headers, SSLSocketFactory sslSocketFactory) throws HttpClientException {
		Assert.notNull(url, "'url' must not be null");
		Assert.notNull(method, "'method' must not be null");
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new HttpClientException("Could not get HttpURLConnection URI: " + e.getMessage(), e);
		}

		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		if (sslSocketFactory != null) {
			clientHttpRequestFactory.setSSLSocketFactory(sslSocketFactory);
		}

		clientHttpRequestFactory.setConnectTimeout(getConnectTimeout());
		clientHttpRequestFactory.setReadTimeout(getReadTimeout());
		ClientHttpResponse response = null;
		try {
			ClientHttpRequest request = clientHttpRequestFactory.createRequest(uri, method);
			if (headers != null) {
				request.getHeaders().putAll(headers);
			}

			if (contentType != null) {
				request.setContentType(contentType);
			}
			if (body != null && body.length != 0) {
				IOUtils.write(body, request.getBody());
			}
			response = request.execute();
			return new SerialzerableClientHttpInputMessage(response);
		} catch (IOException ex) {
			throw createHttpClientResourceAccessException(ex, url, method);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
}
