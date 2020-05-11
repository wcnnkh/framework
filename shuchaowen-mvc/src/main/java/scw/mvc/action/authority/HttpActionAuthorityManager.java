package scw.mvc.action.authority;

import scw.mvc.action.manager.HttpAction;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.HttpAuthorityManager;

public interface HttpActionAuthorityManager extends
		HttpAuthorityManager<HttpAuthority> {
	HttpAuthority getAuthority(HttpAction action);
}
