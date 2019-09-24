package scw.security.authority.http;

import scw.security.authority.SimpleAuthorith;

public class SimpleHttpAuthority extends SimpleAuthorith implements HttpAuthority {
	private static final long serialVersionUID = 1L;
	private String method;
	private String requestPath;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
}
