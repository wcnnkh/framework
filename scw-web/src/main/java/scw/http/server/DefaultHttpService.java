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
		StaticResourceLoader staticResourceLoader = beanFactory
				.isInstance(StaticResourceLoader.class) ? beanFactory
				.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(beanFactory.getEnvironment());
		StaticResourceHttpServiceHandler resourceHandler = new StaticResourceHttpServiceHandler();
		resourceHandler.setResourceLoader(staticResourceLoader);
		getHandlerAccessor().bind(resourceHandler);
		
		for(HttpServiceInterceptor interceptor : beanFactory.getServiceLoader(HttpServiceInterceptor.class)){
			interceptors.add(interceptor);
		}
		
		for(HttpServiceHandler handler : beanFactory.getServiceLoader(HttpServiceHandler.class)){
			getHandlerAccessor().bind(handler);
		}

		if (beanFactory.isInstance(HttpServiceConfigAccessor.class)) {
			setHttpServiceConfigAccessor(beanFactory
					.getInstance(HttpServiceConfigAccessor.class));
		}
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
}
