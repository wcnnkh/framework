package scw.net.http;

import scw.net.Request;

public interface HttpRequest extends Request {
	HttpHeaders getHeaders();

	HttpMethod getMethod();
	
	MediaType getContentType();
}
