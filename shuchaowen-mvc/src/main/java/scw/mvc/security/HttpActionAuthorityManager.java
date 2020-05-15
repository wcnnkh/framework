package scw.mvc.security;

import scw.net.http.server.mvc.action.Action;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.HttpAuthorityManager;

public interface HttpActionAuthorityManager extends
		HttpAuthorityManager<HttpAuthority> {
	HttpAuthority getAuthority(Action action);
}
