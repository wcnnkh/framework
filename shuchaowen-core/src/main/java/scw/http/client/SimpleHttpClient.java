package scw.http.client;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpMethod;

public class SimpleHttpClient extends AbstractHttpClient {

	@Override
	protected ClientHttpRequestBuilder createBuilder(String url,
			HttpMethod method, SSLSocketFactory sslSocketFactory) {
		SimpleClientHttpRequestBuilder builder = new SimpleClientHttpRequestBuilder(
				url, method);
		if (sslSocketFactory != null) {
			builder.setSslSocketFactory(sslSocketFactory);
		}
		return builder;
	}
}
