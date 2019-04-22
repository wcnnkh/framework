package scw.servlet.filter;

import scw.servlet.Request;
import scw.servlet.Response;

public interface FilterChain {
	void doFilter(Request request, Response response) throws Throwable;
}
