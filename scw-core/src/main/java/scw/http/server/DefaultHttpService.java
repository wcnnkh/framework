package scw.http.server;

import java.util.ArrayList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.event.support.DynamicValue;
import scw.http.server.cors.CorsConfig;
import scw.http.server.cors.CorsConfigFactory;
import scw.http.server.cors.DefaultCorsConfigFactory;
import scw.http.server.resource.DefaultStaticResourceLoader;
import scw.http.server.resource.StaticResourceHttpServiceHandler;
import scw.http.server.resource.StaticResourceLoader;
import scw.io.FileUtils;
import scw.value.property.PropertyFactory;

public class DefaultHttpService extends AbstractHttpService {
	//是否开启jsonp支持
	private DynamicValue<Boolean> jsonpEnable;
	//最大的json请求体大小
	private DynamicValue<Long> maxJsonContentLength;
	private CorsConfigFactory corsConfigFactory;
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();
	
	public DefaultHttpService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		jsonpEnable = propertyFactory.getDynamicValue("http.server.jsonp", Boolean.class, false);
		maxJsonContentLength = propertyFactory.getDynamicValue("http.server.json.request.maxContentLength", Long.class, FileUtils.ONE_MB);
		
		StaticResourceLoader staticResourceLoader = beanFactory
				.isInstance(StaticResourceLoader.class) ? beanFactory
				.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(propertyFactory);
		StaticResourceHttpServiceHandler resourceHandler = new StaticResourceHttpServiceHandler(
				staticResourceLoader);
		getHandlerAccessor().bind(resourceHandler);
		interceptors.addAll(InstanceUtils.getConfigurationList(
				HttpServiceInterceptor.class, beanFactory, propertyFactory));

		List<HttpServiceHandler> httpServiceHandlers = InstanceUtils
				.getConfigurationList(HttpServiceHandler.class, beanFactory,
						propertyFactory);
		getHandlerAccessor().bind(httpServiceHandlers);
		
		this.corsConfigFactory = beanFactory.isInstance(CorsConfigFactory.class)
					? beanFactory.getInstance(CorsConfigFactory.class) : new DefaultCorsConfigFactory(propertyFactory);
	}
	
	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
	
	@Override
	public long getMaxJsonContentLength() {
		return maxJsonContentLength.getValue();
	}
	
	protected CorsConfig getCorsConfig(ServerHttpRequest request) {
		return corsConfigFactory.getCorsConfig(request);
	}
	
	@Override
	protected boolean isEnableJsonp(ServerHttpRequest request) {
		return jsonpEnable.getValue();
	}
}
