package scw.mvc.action;

import java.util.Collection;

import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action{
	Collection<HttpControllerConfig> getControllerConfigs();

	HttpAuthority getAuthority();
}
