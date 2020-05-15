package scw.net.http.server.ip;

import scw.net.http.server.ServerHttpRequest;

public interface ServerHttpRequestIpGetter {
	String getRequestIp(ServerHttpRequest request);
}
