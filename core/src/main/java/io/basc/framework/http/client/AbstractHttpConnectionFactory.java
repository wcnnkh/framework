package io.basc.framework.http.client;

import java.util.Map;

import io.basc.framework.env.Sys;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.net.uri.DefaultUriTemplateHandler;
import io.basc.framework.net.uri.UriTemplateHandler;
import io.basc.framework.net.uri.UriUtils;

public abstract class AbstractHttpConnectionFactory extends DefaultHttpClientExecutor implements HttpConnectionFactory {
	private static final UriTemplateHandler URI_TEMPLATE_HANDLER = Sys.env
			.getServiceLoader(UriTemplateHandler.class, DefaultUriTemplateHandler.class).first();
	private UriTemplateHandler uriTemplateHandler;

	public AbstractHttpConnectionFactory() {
	}

	public AbstractHttpConnectionFactory(AbstractHttpConnectionFactory connectionFactory) {
		super(connectionFactory);
		this.uriTemplateHandler = connectionFactory.uriTemplateHandler;
	}

	public UriTemplateHandler getUriTemplateHandler() {
		return uriTemplateHandler == null ? URI_TEMPLATE_HANDLER : uriTemplateHandler;
	}

	public AbstractHttpConnectionFactory setUriTemplateHandler(UriTemplateHandler uriTemplateHandler) {
		this.uriTemplateHandler = uriTemplateHandler;
		return this;
	}

	public HttpConnection createConnection(HttpMethod method, String url) {
		return createConnection(method, UriUtils.toUri(url));
	}

	public HttpConnection createConnection(HttpMethod method, String url, Map<String, ?> uriVariables) {
		return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
	}

	public HttpConnection createConnection(HttpMethod method, String url, Object... uriVariables) {
		return createConnection(method, getUriTemplateHandler().expand(url, uriVariables));
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory.isInstance(UriTemplateHandler.class)) {
			setUriTemplateHandler(serviceLoaderFactory.getInstance(UriTemplateHandler.class));
		}
		super.configure(serviceLoaderFactory);
	}
}