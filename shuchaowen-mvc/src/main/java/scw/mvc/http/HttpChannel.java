package scw.mvc.http;

import scw.mvc.Channel;

public interface HttpChannel extends Channel {
	@SuppressWarnings("unchecked")
	ServerHttpRequest getRequest();

	@SuppressWarnings("unchecked")
	ServerHttpResponse getResponse();
}