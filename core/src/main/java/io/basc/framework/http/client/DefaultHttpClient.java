package io.basc.framework.http.client;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.env.Sys;
import io.basc.framework.net.message.convert.DefaultMessageConverters;
import io.basc.framework.net.message.convert.MessageConverters;
import io.basc.framework.util.ClassUtils;

public class DefaultHttpClient extends AbstractHttpClient {
	private static final Class<?> SIMPLE_CLIENT_HTTP_REQUEST_FACTORY_CLASS = ClassUtils
			.getClass("io.basc.framework.http.client.SimpleClientHttpRequestFactory", null);

	/**
	 * 默认的requestFactory
	 */
	private static final ClientHttpRequestFactory REQUEST_FACTORY = Sys.getEnv()
			.getServiceLoader(ClientHttpRequestFactory.class, SIMPLE_CLIENT_HTTP_REQUEST_FACTORY_CLASS).getServices()
			.first();

	private final MessageConverters messageConverters;
	private final ClientHttpRequestInterceptors interceptors;
	private boolean configured = false;

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
		this.configured = client.configured;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		// 此处不校验是否configured
		configured = true;
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

	@Override
	public boolean isConfigured() {
		return configured;
	}
}
