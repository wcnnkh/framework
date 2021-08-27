package io.basc.framework.security.authority.http;

import io.basc.framework.security.authority.Authority;

public interface HttpAuthority extends Authority {
	String getPath();

	String getMethod();
}
