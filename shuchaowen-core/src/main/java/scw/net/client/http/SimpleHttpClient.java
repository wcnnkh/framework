package scw.net.client.http;

import javax.net.ssl.SSLSocketFactory;

import scw.core.instance.annotation.Configuration;
import scw.net.http.HttpMethod;

@Configuration(order = Integer.MIN_VALUE)
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
