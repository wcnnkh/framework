package io.basc.framework.web.cors;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.web.ServerHttpRequest;

public class CorsUtils {
	public static boolean isCorsRequest(ServerHttpRequest request) {
		return request.getHeaders().getOrigin() != null;
	}

	public static boolean isPreFlightRequest(ServerHttpRequest request) {
		return (isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod())
				&& request.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
	}
}