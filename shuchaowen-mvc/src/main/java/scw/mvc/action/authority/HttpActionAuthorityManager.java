package scw.mvc.action.authority;

import scw.mvc.action.Action;
import scw.security.authority.http.HttpAuthority;
import scw.security.authority.http.HttpAuthorityManager;

public interface HttpActionAuthorityManager extends
		HttpAuthorityManager<HttpAuthority> {
	HttpAuthority getAuthority(Action action);
}
