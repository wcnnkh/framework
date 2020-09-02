package scw.http.server;

import java.io.IOException;

import scw.http.HttpMethod;
import scw.http.server.jsonp.JsonpUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractHttpService implements HttpService{
	public static final String JSONP_CALLBACK = "callback";
	
	private final Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final HttpServiceHandlerAccessor handlerAccessor = new HttpServiceHandlerAccessor();
	
	public void service(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		ServerHttpRequest requestToUse = wrapperRequest(request);
		ServerHttpResponse responseToUse = wrapperResponse(requestToUse, response);

		if (logger.isDebugEnabled()) {
			logger.debug(requestToUse.toString());
		}

		HttpService service = createService(requestToUse, responseToUse);
		try {
			service.service(requestToUse, responseToUse);
		} finally {
			if (!responseToUse.isCommitted()) {
				if (requestToUse.isSupportAsyncControl()) {
					ServerHttpAsyncControl serverHttpAsyncControl = requestToUse.getAsyncControl(responseToUse);
					if (serverHttpAsyncControl.isStarted()) {
						serverHttpAsyncControl.addListener(new ServerHttpResponseCompleteAsyncListener(responseToUse));
						return;
					}
				}
			}
			responseToUse.close();
		}
	}
	
	public HttpServiceHandlerAccessor getHandlerAccessor() {
		return handlerAccessor;
	}

	/**
	 * 每次请求创建的服务
	 * @param request
	 * @param response
	 * @return
	 */
	protected HttpService createService(ServerHttpRequest request, ServerHttpResponse response){
		return new HttpServiceInterceptorChain(getHttpServiceInterceptors().iterator(),
				handlerAccessor);
	}
	
	protected ServerHttpRequest wrapperRequest(ServerHttpRequest request)
			throws IOException {
		if(request.getMethod() == HttpMethod.GET){
			return request;
		}
		
		// 如果是一个json请求，那么包装一下
		if(!(request instanceof JsonServerHttpRequest) && request.getHeaders().isJsonContentType()){
			return new JsonServerHttpRequest(request);
		}
		return request;
	}
	
	protected ServerHttpResponse wrapperResponse(ServerHttpRequest request,
			ServerHttpResponse response) throws IOException {
		if(isEnableJsonp(request)){
			return JsonpUtils.wrapper(request, response);
		}
		return response;
	}
	
	public abstract Iterable<? extends HttpServiceInterceptor> getHttpServiceInterceptors();
	
	protected abstract boolean isEnableJsonp(ServerHttpRequest request);
}
