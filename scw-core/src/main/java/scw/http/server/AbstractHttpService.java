package scw.http.server;

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.http.HttpUtils;
import scw.http.jsonp.JsonpServerHttpResponse;
import scw.http.jsonp.JsonpUtils;
import scw.http.server.cors.CorsConfig;
import scw.http.server.cors.CorsConfigFactory;
import scw.http.server.cors.CorsUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.value.Value;

public abstract class AbstractHttpService implements HttpService, CorsConfigFactory{
	static final String JSONP_DISABLE_PARAMETER_NAME = "_disableJsonp";
	private final Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final HttpServiceHandlerAccessor handlerAccessor = new HttpServiceHandlerAccessor();
	
	public void service(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		CorsConfig config = getCorsConfig(request);
		if(config == null){
			CorsUtils.write(config, response);
		}
		
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
						serverHttpAsyncControl.addListener(new ServerHttpResponseAsyncFlushListener(responseToUse));
						return;
					}
				}
				responseToUse.flush();
			}
			responseToUse.close();
		}
	}
	
	public HttpServiceHandlerAccessor getHandlerAccessor() {
		return handlerAccessor;
	}

	protected abstract Iterable<? extends HttpServiceInterceptor> getHttpServiceInterceptors();

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
	
	protected boolean isJsonRequest(ServerHttpRequest request) throws IOException{
		return request.getHeaders().isJsonContentType()
				&& !(request instanceof JsonServerHttpRequest);
	}
	
	protected ServerHttpRequest wrapperRequest(ServerHttpRequest request)
			throws IOException {
		// 如果是一个json请求，那么包装一下
		if(isJsonRequest(request)){
			return new JsonServerHttpRequest(request);
		}
		return request;
	}
	
	protected boolean isSupportJsonp(ServerHttpRequest request) throws IOException{
		return true;
	}
	
	protected String getJsonpDisableParameterName(){
		return JSONP_DISABLE_PARAMETER_NAME;
	}

	protected ServerHttpResponse wrapperResponse(ServerHttpRequest request,
			ServerHttpResponse response) throws IOException {
		String jsonp = JsonpUtils.getCallback(request);
		if (StringUtils.isNotEmpty(jsonp) && isSupportJsonp(request)) {
			Value value = HttpUtils.getParameter(request, getJsonpDisableParameterName());
			if(value.isEmpty() || value.getAsBooleanValue()){
				return response;
			}
			return new JsonpServerHttpResponse(jsonp, response);
		}
		return response;
	}
}
