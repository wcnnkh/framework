package scw.web;

import java.io.IOException;

@FunctionalInterface
public interface HttpService {
	void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
