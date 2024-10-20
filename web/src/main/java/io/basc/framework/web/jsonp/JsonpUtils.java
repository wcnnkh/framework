package io.basc.framework.web.jsonp;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public final class JsonpUtils {
	private JsonpUtils() {
	};

	public static final String JSONP_RESP_PREFIX = "(";
	public static final String JSONP_RESP_SUFFIX = ");";
	public static final String JSONP_CALLBACK = "callback";
	private static final String JSONP_CALLBACK_VALID = "^[a-zA-Z_]+[\\w0-9_]*$";

	public static ServerHttpResponse wrapper(ServerHttpRequest request, ServerHttpResponse response) {
		if (request.getMethod() != HttpMethod.GET) {
			return response;
		}

		String jsonp = request.getParameterMap().getFirst(JSONP_CALLBACK);
		if (StringUtils.isEmpty(jsonp) || !validCallbackName(jsonp)) {
			return response;
		}

		JsonpServerHttpResponse jsonpServerHttpResponse = XUtils.getDelegate(request, JsonpServerHttpResponse.class);
		if (jsonpServerHttpResponse != null) {
			return response;
		}

		return new JsonpServerHttpResponse(jsonp, response);
	}

	public static boolean validCallbackName(String name) {
		if (name.matches(JSONP_CALLBACK_VALID)) {
			return true;
		}

		return false;
	}
}
