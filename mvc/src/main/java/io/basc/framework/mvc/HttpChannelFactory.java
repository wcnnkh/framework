package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public interface HttpChannelFactory {
	HttpChannel create(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
