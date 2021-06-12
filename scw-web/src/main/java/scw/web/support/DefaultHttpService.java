package scw.web.support;

import java.util.ArrayList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.web.HttpService;
import scw.web.HttpServiceInterceptor;
import scw.web.cors.CorsRegistry;
import scw.web.resource.DefaultStaticResourceLoader;
import scw.web.resource.StaticResourceHttpService;
import scw.web.resource.StaticResourceLoader;

public class DefaultHttpService extends AbstractHttpService {
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();
	private final BeanFactory beanFactory;

	public DefaultHttpService(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory.isInstance(CorsRegistry.class)) {
			setCorsRegistry(beanFactory.getInstance(CorsRegistry.class));
		}

		if (beanFactory.isInstance(NotFoundServiceRegistry.class)) {
			setNotFoundServiceRegistry(beanFactory.getInstance(NotFoundServiceRegistry.class));
		}
		
		if(beanFactory.isInstance(StaticResourceRegistry.class)){
			getServiceRegistry().register(beanFactory.getInstance(StaticResourceRegistry.class));
		}

		StaticResourceLoader staticResourceLoader = beanFactory.isInstance(StaticResourceLoader.class)
				? beanFactory.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(beanFactory.getEnvironment());
		StaticResourceHttpService resourceHandler = new StaticResourceHttpService();
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
