package scw.mvc.support;

import java.util.Collection;

import scw.mvc.Action;
import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action{
	Collection<HttpControllerConfig> getControllerConfigs();

	HttpAuthority getAuthority();
}
