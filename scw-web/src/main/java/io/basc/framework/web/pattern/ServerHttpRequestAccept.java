package io.basc.framework.web.pattern;

import io.basc.framework.util.Accept;
import io.basc.framework.web.ServerHttpRequest;

public interface ServerHttpRequestAccept extends Accept<ServerHttpRequest> {
	boolean accept(ServerHttpRequest request);
}
