package scw.http.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.event.support.DynamicValue;
import scw.http.server.cors.Cors;
import scw.http.server.cors.CorsRegistry;
import scw.http.server.resource.DefaultStaticResourceLoader;
import scw.http.server.resource.StaticResourceHttpServiceHandler;
import scw.http.server.resource.StaticResourceLoader;
import scw.value.property.PropertyFactory;

public class DefaultHttpService extends AbstractHttpService {
	//是否开启jsonp支持
	private DynamicValue<Boolean> jsonpEnable;
	private CorsRegistry corsRegistry;
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();
	
	public DefaultHttpService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		jsonpEnable = propertyFactory.getDynamicValue("http.server.jsonp", Boolean.class, false);
		this.corsRegistry = beanFactory.getInstance(CorsRegistry.class);
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
	}
	
	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}
	
	@Override
	protected boolean isEnableJsonp(ServerHttpRequest request) {
		return jsonpEnable.getValue();
	}
	
	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Cors cors = corsRegistry.getCors(request.getPath());
		if(cors != null){
			cors.write(response.getHeaders());
		}
		super.service(request, response);
	}
}
