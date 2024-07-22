package io.basc.framework.mvc.security;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.register.Registration;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;

public interface HttpActionAuthorityManager extends HttpAuthorityManager<HttpAuthority> {
	HttpAuthority getAuthority(Action action);

	Registration register(Action action);

	void unregister(Action action);
}
