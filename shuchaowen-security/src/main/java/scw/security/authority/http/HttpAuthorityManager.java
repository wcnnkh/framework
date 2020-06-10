package scw.security.authority.http;

import scw.http.HttpMethod;
import scw.security.authority.AuthorityManager;

public interface HttpAuthorityManager<T extends HttpAuthority> extends AuthorityManager<T> {
	T getAuthority(String path, HttpMethod method);
}
