package scw.mvc;

import java.io.IOException;

import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;

public interface HttpChannelFactory {
	HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
