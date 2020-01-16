package scw.security.authority.http;

import scw.net.http.Method;
import scw.security.authority.SimpleAuthority;

public class SimpleHttpAuthority extends SimpleAuthority implements HttpAuthority {
	private static final long serialVersionUID = 1L;
	private String path;
	private Method httpMethod;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Method getHttpMethod() {
		return httpMethod == null ? Method.GET : httpMethod;
	}

	public void setHttpMethod(Method httpMethod) {
		this.httpMethod = httpMethod;
	}
}
