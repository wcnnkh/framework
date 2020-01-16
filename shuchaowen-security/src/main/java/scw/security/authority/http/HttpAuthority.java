package scw.security.authority.http;

import scw.net.http.Method;
import scw.security.authority.Authority;

public interface HttpAuthority extends Authority {
	String getPath();

	Method getHttpMethod();
}
