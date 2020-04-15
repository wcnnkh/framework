package scw.mvc.action.authority;

import scw.mvc.action.manager.HttpAction;
import scw.security.authority.http.HttpAuthorityManager;

public interface HttpActionAuthorityManager extends
		ActionAuthorityManager<HttpActionAuthority, HttpAction>, HttpAuthorityManager<HttpActionAuthority>{
}
