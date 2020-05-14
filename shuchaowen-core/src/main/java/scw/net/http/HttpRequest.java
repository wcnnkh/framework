package scw.net.http;

import java.net.URI;

import scw.net.message.Message;

public interface HttpRequest extends Message {
	HttpHeaders getHeaders();

	HttpMethod getMethod();
	
	MediaType getContentType();
	
	URI getURI();
}
