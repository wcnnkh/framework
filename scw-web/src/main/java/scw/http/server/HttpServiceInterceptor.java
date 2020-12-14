package scw.http.server;

import java.io.IOException;

public interface HttpServiceInterceptor {
	void intercept(ServerHttpRequest request, ServerHttpResponse response, HttpService httpService) throws IOException;
}
