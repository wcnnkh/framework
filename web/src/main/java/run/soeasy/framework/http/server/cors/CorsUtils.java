package run.soeasy.framework.http.server.cors;

import run.soeasy.framework.http.HttpHeaders;
import run.soeasy.framework.http.HttpMethod;
import run.soeasy.framework.http.server.ServerHttpRequest;

public class CorsUtils {
	public static boolean isCorsRequest(ServerHttpRequest request) {
		return request.getHeaders().getOrigin() != null;
	}

	public static boolean isPreFlightRequest(ServerHttpRequest request) {
		return (isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod())
				&& request.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
	}
}