package scw.security.authority.http;

import scw.core.utils.StringUtils;
import scw.net.http.Method;
import scw.security.authority.SimpleAuthority;

public class SimpleHttpAuthority extends SimpleAuthority implements HttpAuthority {
	private static final long serialVersionUID = 1L;
	private String path;
	private String method;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return StringUtils.isEmpty(method) ? Method.GET.name() : method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
