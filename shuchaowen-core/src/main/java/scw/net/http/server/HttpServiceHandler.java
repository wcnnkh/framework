package scw.net.http.server;

import java.io.IOException;

public interface HttpServiceHandler {
	boolean accept(ServerHttpRequest request);

	void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
