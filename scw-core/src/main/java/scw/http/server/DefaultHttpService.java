package scw.http.server;

import java.io.IOException;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.event.support.DynamicValue;
import scw.http.server.cors.CorsServiceInterceptor;
import scw.http.server.resource.DefaultStaticResourceLoader;
import scw.http.server.resource.StaticResourceHttpServiceHandler;
import scw.http.server.resource.StaticResourceLoader;
import scw.io.FileUtils;
import scw.value.property.PropertyFactory;

public class DefaultHttpService extends AbstractHttpService {
	//是否开启jsonp支持
	private DynamicValue<Boolean> supportJsonp;
	//最大的json请求体大小
	private DynamicValue<Long> maxJsonContentLength;
	private DynamicValue<String> jsonpDisableParameterName;
	
	public DefaultHttpService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		supportJsonp = propertyFactory.getDynamicValue("http.server.jsonp", Boolean.class, true);
		maxJsonContentLength = propertyFactory.getDynamicValue("http.server.json.request.maxContentLength", Long.class, FileUtils.ONE_MB);
		jsonpDisableParameterName = propertyFactory.getDynamicValue("http.server.jsonp.disable.parameterName", String.class, JSONP_DISABLE_PARAMETER_NAME);
		
		StaticResourceLoader staticResourceLoader = beanFactory
				.isInstance(StaticResourceLoader.class) ? beanFactory
				.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(propertyFactory);
		StaticResourceHttpServiceHandler resourceHandler = new StaticResourceHttpServiceHandler(
				staticResourceLoader);
		getHandlerAccessor().bind(resourceHandler);
		getInterceptors().add(new CorsServiceInterceptor(beanFactory,
				propertyFactory));
		getInterceptors().addAll(InstanceUtils.getConfigurationList(
				HttpServiceInterceptor.class, beanFactory, propertyFactory));

		List<HttpServiceHandler> httpServiceHandlers = InstanceUtils
				.getConfigurationList(HttpServiceHandler.class, beanFactory,
						propertyFactory);
		getHandlerAccessor().bind(httpServiceHandlers);
	}
	
	@Override
	protected boolean isJsonRequest(ServerHttpRequest request) throws IOException {
		if(request.getContentLength() > maxJsonContentLength.getValue()){
			return false;
		}
		
		return super.isJsonRequest(request);
	}
	
	@Override
	protected boolean isSupportJsonp(ServerHttpRequest request)
			throws IOException {
		return supportJsonp.getValue() && super.isSupportJsonp(request);
	}
	
	public String getDisableJsonpParameterName() {
		return jsonpDisableParameterName.getValue();
	}
}
