package io.basc.framework.web.support;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.HttpServiceInterceptor;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.resource.StaticResourceHttpService;
import io.basc.framework.web.resource.StaticResourceRegistry;

public class DefaultHttpService extends AbstractHttpService {
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();

	public DefaultHttpService(ServiceLoaderFactory factory) {
		super(factory.getInstance(HttpServiceRegistry.class));
		if (factory.isInstance(CorsRegistry.class)) {
			setCorsRegistry(factory.getInstance(CorsRegistry.class));
		}

		if (factory.isInstance(NotFoundServiceRegistry.class)) {
			setNotFoundServiceRegistry(factory.getInstance(NotFoundServiceRegistry.class));
		}

		getServiceRegistry().add(new StaticResourceHttpService(factory));
		if (factory.isInstance(StaticResourceRegistry.class)) {
			getServiceRegistry().add(factory.getInstance(StaticResourceRegistry.class));
		}

		for (HttpServiceInterceptor interceptor : factory.getServiceLoader(HttpServiceInterceptor.class)) {
			interceptors.add(interceptor);
		}

		for (HttpService handler : factory.getServiceLoader(HttpService.class)) {
			getServiceRegistry().add(handler);
		}
	}

	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
}
