package scw.security.authority.http;

import scw.http.HttpMethod;
import scw.security.authority.AuthorityManager;

public interface HttpAuthorityManager<T extends HttpAuthority> extends
		AuthorityManager<T> {
	T getAuthority(String path, String method);

	default T getAuthority(String path, HttpMethod method) {
		return getAuthority(path, method == null ? null : method.name());
	}
}
