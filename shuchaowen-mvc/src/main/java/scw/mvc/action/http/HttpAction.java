package scw.mvc.action.http;

import java.util.Collection;

import scw.mvc.action.Action;
import scw.net.http.HttpMethod;

public interface HttpAction extends Action{
	String getController();
	
	Collection<HttpMethod> getHttpMethods();
}
