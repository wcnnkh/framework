package scw.web.servlet.action;

import scw.web.servlet.Request;
import scw.web.servlet.Response;

public interface FilterChain{
	public void doFilter(Request request, Response response) throws Throwable;
}
