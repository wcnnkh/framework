package io.basc.framework.http;

import java.net.URI;

public interface HttpRequest extends HttpMessage {
	HttpHeaders getHeaders();

	HttpMethod getMethod();
	
	URI getURI();
}
