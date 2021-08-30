package io.basc.framework.web.support;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.HttpServiceInterceptor;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.resource.StaticResourceHttpService;
import io.basc.framework.web.resource.StaticResourceRegistry;

public class DefaultHttpService extends AbstractHttpService {
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();

	public DefaultHttpService(BeanFactory beanFactory) {
		super(beanFactory.getInstance(HttpServiceRegistry.class));
		if (beanFactory.isInstance(CorsRegistry.class)) {
			setCorsRegistry(beanFactory.getInstance(CorsRegistry.class));
		}

		if (beanFactory.isInstance(NotFoundServiceRegistry.class)) {
			setNotFoundServiceRegistry(beanFactory.getInstance(NotFoundServiceRegistry.class));
		}

		getServiceRegistry().add(new StaticResourceHttpService(beanFactory));
		if (beanFactory.isInstance(StaticResourceRegistry.class)) {
			getServiceRegistry().add(beanFactory.getInstance(StaticResourceRegistry.class));
		}

		for (HttpServiceInterceptor interceptor : beanFactory.getServiceLoader(HttpServiceInterceptor.class)) {
			interceptors.add(interceptor);
		}

		for (HttpService handler : beanFactory.getServiceLoader(HttpService.class)) {
			getServiceRegistry().add(handler);
		}
	}

	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
}
