package run.soeasy.framework.http.client;

import lombok.NonNull;
import run.soeasy.framework.net.convert.support.ConfigurableMessageConverter;
import run.soeasy.framework.net.convert.support.DefaultMessageConverters;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.reflect.ReflectionApi;
import run.soeasy.framework.util.spi.NativeServiceLoader;
import run.soeasy.framework.util.spi.ServiceLoaderDiscovery;

public class DefaultHttpClient extends AbstractHttpClient {
	private static final Class<?> SIMPLE_CLIENT_HTTP_REQUEST_FACTORY_CLASS = ClassUtils
			.getClass("io.basc.framework.http.client.SimpleClientHttpRequestFactory", null);

	/**
	 * 默认的requestFactory
	 */
	private static final ClientHttpRequestFactory REQUEST_FACTORY = NativeServiceLoader
			.load(ClientHttpRequestFactory.class).findFirst().orElseGet(() -> (ClientHttpRequestFactory) ReflectionApi
					.newInstance(SIMPLE_CLIENT_HTTP_REQUEST_FACTORY_CLASS));

	private final ConfigurableMessageConverter messageConverters;
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
	public Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery) {
		interceptors.doConfigure(discovery);
		messageConverters.doConfigure(discovery);
		return super.doConfigure(discovery);
	}

	public final ClientHttpRequestInterceptors getInterceptors() {
		return interceptors;
	}

	public ConfigurableMessageConverter getMessageConverters() {
		return messageConverters;
	}

	@Override
	public DefaultHttpClient clone() {
		return new DefaultHttpClient(this);
	}

}
