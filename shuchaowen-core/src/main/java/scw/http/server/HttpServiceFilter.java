package scw.http.server;

import java.io.IOException;

public interface HttpServiceFilter {
	void doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService) throws IOException;
}
