package scw.mvc.http;

import scw.mvc.Channel;

public interface HttpChannel extends Channel {
	@SuppressWarnings("unchecked")
	HttpRequest getRequest();

	@SuppressWarnings("unchecked")
	HttpResponse getResponse();
}