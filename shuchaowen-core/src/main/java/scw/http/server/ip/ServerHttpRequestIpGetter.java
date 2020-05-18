package scw.http.server.ip;

import scw.http.server.ServerHttpRequest;

public interface ServerHttpRequestIpGetter {
	String getRequestIp(ServerHttpRequest request);
}
