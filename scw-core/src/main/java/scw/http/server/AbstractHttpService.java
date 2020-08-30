package scw.http.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import scw.core.utils.StringUtils;
import scw.http.HttpUtils;
import scw.http.jsonp.JsonpServerHttpResponse;
import scw.http.jsonp.JsonpUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractHttpService implements HttpService{
	static final String JSONP_DISABLE_PARAMETER_NAME = "_disableJsonp";
	
	private final Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final HttpServiceHandlerAccessor handlerAccessor = new HttpServiceHandlerAccessor();
	private final List<HttpServiceInterceptor> interceptors = new ArrayList<HttpServiceInterceptor>();
	
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


	public List<HttpServiceInterceptor> getInterceptors() {
		return interceptors;
	}

	/**
	 * 每次请求创建的服务
	 * @param request
	 * @param response
	 * @return
	 */
	protected HttpService createService(ServerHttpRequest request, ServerHttpResponse response){
		return new HttpServiceInterceptorChain(interceptors.iterator(),
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
		return !HttpUtils.getParameter(request, getJsonpDisableParameterName()).getAsBooleanValue();
	}
	
	protected String getJsonpDisableParameterName(){
		return JSONP_DISABLE_PARAMETER_NAME;
	}

	protected ServerHttpResponse wrapperResponse(ServerHttpRequest request,
			ServerHttpResponse response) throws IOException {
		String jsonp = JsonpUtils.getCallback(request);
		if (StringUtils.isNotEmpty(jsonp) && isSupportJsonp(request)) {
			return new JsonpServerHttpResponse(jsonp, response);
		}
		return response;
	}
}
