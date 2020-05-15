package scw.net.http.server;

import java.io.IOException;

public interface HttpServiceFilter {
	void doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService) throws IOException;
}
