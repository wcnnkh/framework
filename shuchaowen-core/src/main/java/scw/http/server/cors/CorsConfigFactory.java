package scw.http.server.cors;

import scw.http.server.ServerHttpRequest;

public interface CorsConfigFactory {
	CorsConfig getCorsConfig(ServerHttpRequest request);
}
