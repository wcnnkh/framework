package scw.mvc.support.action;

import java.util.Collection;

import scw.mvc.Action;
import scw.net.http.Method;
import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action{
	Collection<Method> getHttpMethods();
	
	HttpAuthority getAuthority();
}
