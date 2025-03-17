package run.soeasy.framework.http.server.jsonp;

import run.soeasy.framework.http.HttpMethod;
import run.soeasy.framework.http.server.ServerHttpRequest;
import run.soeasy.framework.http.server.ServerHttpResponse;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.function.Wrapper;

public final class JsonpUtils {
	private JsonpUtils() {
	};

	public static final String JSONP_RESP_PREFIX = "(";
	public static final String JSONP_RESP_SUFFIX = ");";
	public static final String JSONP_CALLBACK = "callback";
	private static final String JSONP_CALLBACK_VALID = "^[a-zA-Z_]+[\\w0-9_]*$";

	public static ServerHttpResponse wrap(ServerHttpRequest request, ServerHttpResponse response) {
		if (request.getMethod() != HttpMethod.GET) {
			return response;
		}

		String jsonp = request.getParameterMap().getFirst(JSONP_CALLBACK);
		if (StringUtils.isEmpty(jsonp) || !validCallbackName(jsonp)) {
			return response;
		}

		return Wrapper.isWrapperFor(response, JsonpServerHttpResponse.class) ? response
				: new JsonpServerHttpResponse<>(response, jsonp);
	}

	public static boolean validCallbackName(String name) {
		if (name.matches(JSONP_CALLBACK_VALID)) {
			return true;
		}

		return false;
	}
}
