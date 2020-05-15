package scw.net.http.server.cors;

import java.io.IOException;

import scw.net.http.server.HttpService;
import scw.net.http.server.HttpServiceFilter;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public final class CorsFilter implements HttpServiceFilter {
	public static final int ORDER = 1000;

	private final CorsConfigFactory corsConfigFactory;

	public CorsFilter(CorsConfigFactory corsConfigFactory) {
		this.corsConfigFactory = corsConfigFactory;
	}

	public void doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService)
			throws IOException {
		if (corsConfigFactory != null) {
			CorsConfig corsConfig = corsConfigFactory.getCorsConfig(request);
			if (corsConfig != null) {
				CorsUtils.write(corsConfig, response);
			}
		}
		httpService.service(request, response);
	}
}
