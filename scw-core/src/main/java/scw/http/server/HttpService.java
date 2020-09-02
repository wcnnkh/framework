package scw.http.server;

import java.io.IOException;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface HttpService {
	void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}