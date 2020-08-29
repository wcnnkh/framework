package scw.http.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.http.jsonp.JsonpServerHttpResponse;
import scw.http.jsonp.JsonpUtils;
import scw.http.server.cors.CorsServiceInterceptor;
import scw.http.server.resource.DefaultStaticResourceLoader;
import scw.http.server.resource.StaticResourceHttpServiceHandler;
import scw.http.server.resource.StaticResourceLoader;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.property.PropertyFactory;

public class DefaultHttpService implements HttpService {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultHttpService.class);
	private final HttpServiceHandlerAccessor handlerAccessor = new HttpServiceHandlerAccessor();
	private List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();

	public DefaultHttpService() {
	}

	public DefaultHttpService(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		StaticResourceLoader staticResourceLoader = beanFactory
				.isInstance(StaticResourceLoader.class) ? beanFactory
				.getInstance(StaticResourceLoader.class)
				: new DefaultStaticResourceLoader(propertyFactory);
		StaticResourceHttpServiceHandler resourceHandler = new StaticResourceHttpServiceHandler(
				staticResourceLoader);
		handlerAccessor.bind(resourceHandler);
		interceptors.add(new CorsServiceInterceptor(beanFactory,
				propertyFactory));
		interceptors.addAll(InstanceUtils.getConfigurationList(
				HttpServiceInterceptor.class, beanFactory, propertyFactory));
		interceptors = Arrays.asList(interceptors
				.toArray(new HttpServiceInterceptor[0]));

		List<HttpServiceHandler> httpServiceHandlers = InstanceUtils
				.getConfigurationList(HttpServiceHandler.class, beanFactory,
						propertyFactory);
		handlerAccessor.bind(httpServiceHandlers);
	}

	public final HttpServiceHandlerAccessor getHandlerAccessor() {
		return handlerAccessor;
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		ServerHttpRequest requestToUse = wrapperRequest(request);
		ServerHttpResponse responseToUse = wrapperResponse(requestToUse, response);

		if (logger.isDebugEnabled()) {
			logger.debug(requestToUse.toString());
		}

		HttpService service = createHttpService();
		try {
			service.service(requestToUse, responseToUse);
		} finally {
			if (!responseToUse.isCommitted()) {
				if (requestToUse.isSupportAsyncControl()) {
					ServerHttpAsyncControl serverHttpAsyncControl = requestToUse.getAsyncControl(responseToUse);
					if (serverHttpAsyncControl.isStarted()) {
						serverHttpAsyncControl.addListener(new ServerHttpResponseAsyncFlushListener(responseToUse));
						return;
					}
				}
				responseToUse.flush();
			}
			responseToUse.close();
		}
	}

	// 每次请求得到使用的服务
	protected HttpService createHttpService() {
		return new HttpServiceInterceptorChain(interceptors.iterator(),
				handlerAccessor);
	}

	public List<HttpServiceInterceptor> getHttpServiceInterceptors() {
		return interceptors;
	}

	protected ServerHttpRequest wrapperRequest(ServerHttpRequest request)
			throws IOException {
		// 如果是一个json请求，那么包装一下
		if (request.getHeaders().isJsonContentType()
				&& !(request instanceof JsonServerHttpRequest)) {
			return new JsonServerHttpRequest(request);
		}
		return request;
	}

	protected ServerHttpResponse wrapperResponse(ServerHttpRequest request,
			ServerHttpResponse response) throws IOException {
		String jsonp = JsonpUtils.getCallback(request);
		if (StringUtils.isNotEmpty(jsonp)) {
			return new JsonpServerHttpResponse(jsonp, response);
		}
		return response;
	}
}
