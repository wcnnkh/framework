package io.basc.framework.http.client;

import io.basc.framework.env.Sys;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;

public class DefaultHttpClient extends AbstractHttpClient {
	/**
	 * 默认的requestFactory
	 */
	private static final ClientHttpRequestFactory REQUEST_FACTORY = Sys.env.getServiceLoader(
			ClientHttpRequestFactory.class, "io.basc.framework.http.client.SimpleClientHttpRequestFactory").first();

	private final MessageConverters messageConverters;
	private final ClientHttpRequestInterceptors interceptors;

	public DefaultHttpClient() {
		super(REQUEST_FACTORY);
		this.messageConverters = new DefaultMessageConverters();
		this.interceptors = new ClientHttpRequestInterceptors();
		super.setInterceptor(interceptors);
		setMessageConverter(messageConverters);
		setResponseErrorHandler(DefaultClientHttpResponseErrorHandler.INSTANCE);
	}

	protected DefaultHttpClient(DefaultHttpClient client) {
		super(client);
		this.messageConverters = client.messageConverters;
		this.interceptors = client.interceptors;
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

	@Override
	public DefaultHttpClient clone() {
		return new DefaultHttpClient(this);
	}
}
