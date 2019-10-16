package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Channel;
import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action<Channel> {
	Collection<HttpControllerConfig> getControllerConfigs();

	HttpAuthority getAuthority();
}
