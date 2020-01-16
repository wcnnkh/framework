package scw.net.http;

import scw.net.Request;

public interface HttpRequest extends Request {
	HttpHeaders getHeaders();

	Method getMethod();
	
	MediaType getContentType();
}
