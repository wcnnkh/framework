package scw.web.servlet.action;

import scw.web.servlet.Request;
import scw.web.servlet.Response;

public interface Filter {
	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
