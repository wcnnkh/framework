package scw.security.authority.http;

import scw.security.authority.Authority;

public interface HttpAuthority extends Authority {
	String getMethod();

	String getRequestPath();
}
