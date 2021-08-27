package io.basc.framework.web.message;

import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

import java.io.IOException;

public interface WebMessageWriter {
	void write(ServerHttpRequest request, ServerHttpResponse response) throws IOException;
}
