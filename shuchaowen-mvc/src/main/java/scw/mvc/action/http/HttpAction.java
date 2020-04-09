package scw.mvc.action.http;

import java.util.Collection;

import scw.mvc.action.Action;
import scw.net.http.HttpMethod;
import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action{
	Collection<HttpMethod> getHttpMethods();
	
	HttpAuthority getAuthority();
}
