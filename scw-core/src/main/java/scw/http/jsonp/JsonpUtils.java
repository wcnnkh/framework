package scw.http.jsonp;

import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;

public final class JsonpUtils {
	private JsonpUtils(){};
	public static final String JSONP_RESP_PREFIX = "(";
	public static final String JSONP_RESP_SUFFIX = ");";
	public static final String JSONP_CALLBACK = "callback";
	
	
	public static ServerHttpResponse wrapper(ServerHttpRequest request, ServerHttpResponse response){
		if(request.getMethod() != HttpMethod.GET){
			return response;
		}
		
		if(response instanceof JsonpServerHttpResponse){
			return response;
		}
		
		String jsonp = request.getParameterMap().getFirst(JSONP_CALLBACK);
		if(StringUtils.isEmpty(jsonp)){
			return response;
		}
		
		return new JsonpServerHttpResponse(jsonp, response);
	}
}
