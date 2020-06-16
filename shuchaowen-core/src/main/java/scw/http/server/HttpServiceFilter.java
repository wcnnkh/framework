package scw.http.server;

import java.io.IOException;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface HttpServiceFilter {
	void doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService) throws IOException;
}
