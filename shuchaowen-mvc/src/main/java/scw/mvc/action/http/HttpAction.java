package scw.mvc.action.http;

import java.util.Collection;

import scw.mvc.action.Action;
import scw.net.http.Method;
import scw.security.authority.http.HttpAuthority;

public interface HttpAction extends Action{
	Collection<Method> getHttpMethods();
	
	HttpAuthority getAuthority();
}
