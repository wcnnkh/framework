package scw.web.pattern;

import scw.util.Accept;
import scw.web.ServerHttpRequest;

public interface ServerHttpRequestAccept extends Accept<ServerHttpRequest> {
	boolean accept(ServerHttpRequest request);
}
