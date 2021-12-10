package io.basc.framework.http;

import java.net.URI;

public interface HttpRequest extends HttpMessage {
	HttpHeaders getHeaders();

	default HttpMethod getMethod() {
		return HttpMethod.resolve(getRawMethod());
	}

	String getRawMethod();

	URI getURI();
}
