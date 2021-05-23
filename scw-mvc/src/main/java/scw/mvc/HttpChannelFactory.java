package scw.mvc;

import java.io.IOException;

import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public interface HttpChannelFactory {
	HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
