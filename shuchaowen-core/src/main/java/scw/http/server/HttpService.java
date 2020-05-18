package scw.http.server;

import java.io.IOException;

public interface HttpService {
	void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
