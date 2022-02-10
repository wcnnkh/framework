package io.basc.framework.http.client;

import java.net.URI;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;

public class DefaultHttpClient extends AbstractHttpConnectionFactory implements HttpClient {
	protected final MessageConverters messageConverters = new DefaultMessageConverters();
	private final ClientHttpRequestInterceptors interceptors = new ClientHttpRequestInterceptors();

	public DefaultHttpClient() {
		super.setInterceptor(interceptors);
		setMessageConverter(messageConverters);
		setResponseErrorHandler(DefaultClientHttpResponseErrorHandler.INSTANCE);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		interceptors.configure(serviceLoaderFactory);
		messageConverters.configure(serviceLoaderFactory);
		super.configure(serviceLoaderFactory);
	}

	public final ClientHttpRequestInterceptors getInterceptors() {
		return interceptors;
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}
}
