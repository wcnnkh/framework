package scw.servlet.action;

import scw.servlet.Request;
import scw.servlet.Response;

public interface Filter {
	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
