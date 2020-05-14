package scw.net.http.server.cors;

import scw.net.http.server.ServerHttpRequest;

public interface CorsConfigFactory {
	CorsConfig getCorsConfig(ServerHttpRequest request);
}
