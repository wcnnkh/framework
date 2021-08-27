package io.basc.framework.security.authority.http;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.security.authority.AuthorityManager;

public interface HttpAuthorityManager<T extends HttpAuthority> extends
		AuthorityManager<T> {
	T getAuthority(String path, String method);

	default T getAuthority(String path, HttpMethod method) {
		return getAuthority(path, method == null ? null : method.name());
	}
}
