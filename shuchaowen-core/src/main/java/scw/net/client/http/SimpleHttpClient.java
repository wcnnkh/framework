package scw.net.client.http;

import javax.net.ssl.SSLSocketFactory;

import scw.beans.BeanUtils;
import scw.core.instance.InstanceFactory;
import scw.net.http.HttpMethod;
import scw.net.message.converter.MessageConverter;

public class SimpleHttpClient extends AbstractHttpClient {
	public SimpleHttpClient() {
	}

	public SimpleHttpClient(InstanceFactory instanceFactory) {
		getMessageConverter().addAll(
				BeanUtils.getConfigurationList(MessageConverter.class,
						instanceFactory));
	}

	@Override
	protected ClientHttpRequestBuilder createBuilder(String url, HttpMethod method,
			SSLSocketFactory sslSocketFactory) {
		SimpleClientHttpRequestBuilder builder = new SimpleClientHttpRequestBuilder(
				url, method);
		if (sslSocketFactory != null) {
			builder.setSslSocketFactory(sslSocketFactory);
		}
		return builder;
	}
}
