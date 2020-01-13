package scw.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;

import scw.http.HttpInputMessage;
import scw.http.HttpRequest;
import scw.util.MultiValueMap;

public interface ServerHttpRequest extends HttpRequest, HttpInputMessage {
	Principal getPrincipal();

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();
	
	boolean isSupportAsyncRequestControl();
	
	ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse response);
	
	MultiValueMap<String, String> getParameterMap();
}
