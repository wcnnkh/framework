package scw.net.client.http;

import javax.net.ssl.SSLSocketFactory;

import scw.net.http.Method;

public class SimpleHttpClient extends AbstractHttpClient {

	@Override
	protected ClientHttpRequestBuilder createBuilder(String url, Method method,
			SSLSocketFactory sslSocketFactory) {
		SimpleClientHttpRequestBuilder builder = new SimpleClientHttpRequestBuilder(
				url, method);
		if (sslSocketFactory != null) {
			builder.setSslSocketFactory(sslSocketFactory);
		}
		return builder;
	}
}
