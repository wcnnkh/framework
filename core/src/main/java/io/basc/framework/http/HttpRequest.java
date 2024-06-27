package io.basc.framework.http;

import io.basc.framework.net.Request;

public interface HttpRequest extends HttpMessage, Request {
	HttpHeaders getHeaders();

	default HttpMethod getMethod() {
		return HttpMethod.resolve(getRawMethod());
	}

	String getRawMethod();
}
