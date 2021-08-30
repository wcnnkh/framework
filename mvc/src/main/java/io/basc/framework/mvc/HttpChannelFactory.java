package io.basc.framework.mvc;

import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

public interface HttpChannelFactory {
	HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
