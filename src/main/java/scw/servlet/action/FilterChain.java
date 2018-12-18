package scw.servlet.action;

import scw.servlet.Request;
import scw.servlet.Response;

public interface FilterChain{
	public void doFilter(Request request, Response response) throws Throwable;
}
