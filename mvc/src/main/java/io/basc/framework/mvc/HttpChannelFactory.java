package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;

public interface HttpChannelFactory {
	HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
