package scw.security.authority.http;

import scw.net.http.HttpMethod;
import scw.security.authority.SimpleAuthority;

public class SimpleHttpAuthority extends SimpleAuthority implements HttpAuthority {
	private static final long serialVersionUID = 1L;
	private String path;
	private HttpMethod httpMethod;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod == null ? HttpMethod.GET : httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
}
