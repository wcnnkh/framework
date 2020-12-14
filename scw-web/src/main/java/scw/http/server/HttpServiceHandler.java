package scw.http.server;

import java.io.IOException;

public interface HttpServiceHandler {
	void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
