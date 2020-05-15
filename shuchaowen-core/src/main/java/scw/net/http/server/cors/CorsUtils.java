package scw.net.http.server.cors;

import scw.core.utils.StringUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpMethod;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public class CorsUtils {
	/**
	 * 是否是一个有效的cors请求
	 */
	public static boolean isCorsRequest(ServerHttpRequest request) {
		return request.getHeaders().getOrigin() != null;
	}

	/**
	 * 是否是一个cors前置请求(OPTIONS请求)
	 */
	public static boolean isPreFlightRequest(ServerHttpRequest request) {
		return (isCorsRequest(request) && HttpMethod.OPTIONS.matches(request.getMethod())
				&& request.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
	}

	public static void write(CorsConfig config, ServerHttpResponse serverHttpResponse) {
		/* 允许跨域的主机地址 */
		if (StringUtils.isNotEmpty(config.getOrigin())) {
			serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, config.getOrigin());
		}

		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		if (StringUtils.isNotEmpty(config.getMethods())) {
			serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, config.getMethods());
		}

		/* 重新预检验跨域的缓存时间 (s) */
		if (config.getMaxAge() > 0) {
			serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, config.getMaxAge() + "");
		}

		/* 允许跨域的请求头 */
		if (StringUtils.isNotEmpty(config.getHeaders())) {
			serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, config.getHeaders());
		}

		/* 是否携带cookie */
		serverHttpResponse.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, config.isCredentials() + "");
	}
}
