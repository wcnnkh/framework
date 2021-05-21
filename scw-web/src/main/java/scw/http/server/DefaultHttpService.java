package scw.http.server;

import java.util.ArrayList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.http.server.cors.CorsRegistry;
import scw.http.server.resource.DefaultStaticResourceLoader;
import scw.http.server.resource.StaticResourceHttpServiceHandler;
import scw.http.server.resource.StaticResourceLoader;

public class DefaultHttpService extends AbstractHttpService {
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();
	private final BeanFactory beanFactory;

	public DefaultHttpService(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory.isInstance(CorsRegistry.class)) {
			setCorsRegistry(beanFactory.getInstance(CorsRegistry.class));
		}
		StaticResourceLoader staticResourceLoader = beanFactory.isInstance(StaticResourceLoader.class)
				? beanFactory.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(beanFactory.getEnvironment());
		StaticResourceHttpServiceHandler resourceHandler = new StaticResourceHttpServiceHandler();
		resourceHandler.setResourceLoader(staticResourceLoader);
		getServiceRegistry().register(resourceHandler);

		for (HttpServiceInterceptor interceptor : beanFactory.getServiceLoader(HttpServiceInterceptor.class)) {
			interceptors.add(interceptor);
		}

		for (HttpService handler : beanFactory.getServiceLoader(HttpService.class)) {
			getServiceRegistry().register(handler);
		}
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
}
