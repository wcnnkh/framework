package scw.http.server.pattern;

import scw.http.server.ServerHttpRequest;
import scw.util.Accept;

public interface ServerHttpRequestAccept extends Accept<ServerHttpRequest> {
	boolean accept(ServerHttpRequest request);
}
