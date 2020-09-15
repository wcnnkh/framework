package scw.http.server;

import java.io.IOException;

import scw.http.HttpMethod;
import scw.http.HttpUtils;
import scw.http.server.jsonp.JsonpUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.XUtils;

public abstract class AbstractHttpService implements HttpService {
	public static final String JSONP_CALLBACK = "callback";
	private static Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final HttpServiceHandlerAccessor handlerAccessor = new HttpServiceHandlerAccessor();
	private HttpServiceConfigAccessor httpServiceConfigAccessor;

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		ServerHttpRequest requestToUse = wrapperRequest(request, getHttpServiceConfigAccessor());
		ServerHttpResponse responseToUse = wrapperResponse(requestToUse, response, getHttpServiceConfigAccessor());

		if (logger.isDebugEnabled()) {
			logger.debug(requestToUse);
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
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected HttpService createService(ServerHttpRequest request, ServerHttpResponse response) {
		return new HttpServiceInterceptorChain(getHttpServiceInterceptors().iterator(), handlerAccessor);
	}
	
	protected ServerHttpRequest wrapperRequest(ServerHttpRequest request, HttpServiceConfigAccessor configAccessor) throws IOException {
		if (request.getMethod() == HttpMethod.GET) {
			return request;
		}
		
		// 如果是一个json请求，那么包装一下
		if(request.getHeaders().isJsonContentType() && (configAccessor == null || configAccessor.isSupportJsonWrapper(request.getPath()))){
			JsonServerHttpRequest jsonServerHttpRequest = XUtils.getTarget(request, JsonServerHttpRequest.class);
			if(jsonServerHttpRequest != null){
				//返回原始对象
				return request;
			}
			
			return new JsonServerHttpRequest(request);
		}

		// 如果是 一个MultiParty请求，那么包装一下
		if(request.getHeaders().isMultipartFormContentType()){
			MultiPartServerHttpRequest multiPartServerHttpRequest = XUtils.getTarget(request, MultiPartServerHttpRequest.class);
			if(multiPartServerHttpRequest != null){
				//返回原始对象
				return request;
			}
			
			if (HttpUtils.isSupportMultiPart()) {
				return new MultiPartServerHttpRequest(request);
			} else {
				logger.warn("Multipart is not supported: {}", request);
			}
		}
		return request;
	}

	protected ServerHttpResponse wrapperResponse(ServerHttpRequest request, ServerHttpResponse response, HttpServiceConfigAccessor configAccessor)
			throws IOException {
		if (isEnableJsonp(request) && (configAccessor == null || configAccessor.isSupportJsonp(request.getPath()))) {
			return JsonpUtils.wrapper(request, response);
		}
		return response;
	}

	public HttpServiceConfigAccessor getHttpServiceConfigAccessor() {
		return httpServiceConfigAccessor;
	}

	public void setHttpServiceConfigAccessor(
			HttpServiceConfigAccessor httpServiceConfigAccessor) {
		this.httpServiceConfigAccessor = httpServiceConfigAccessor;
	}

	public abstract Iterable<? extends HttpServiceInterceptor> getHttpServiceInterceptors();

	protected abstract boolean isEnableJsonp(ServerHttpRequest request);
}
