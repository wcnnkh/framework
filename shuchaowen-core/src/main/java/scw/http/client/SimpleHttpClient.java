package scw.http.client;

import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import scw.http.HttpMethod;

public class SimpleHttpClient extends AbstractHttpClient {

	@Override
	protected ClientHttpRequestBuilder createBuilder(URI uri,
			HttpMethod method, SSLSocketFactory sslSocketFactory) {
		SimpleClientHttpRequestBuilder builder = new SimpleClientHttpRequestBuilder(
				uri, method);
		if (sslSocketFactory != null) {
			builder.setSslSocketFactory(sslSocketFactory);
		}
		
		builder.setConfig(this);
		return builder;
	}
}
