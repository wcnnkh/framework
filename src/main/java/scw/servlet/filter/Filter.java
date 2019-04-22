package scw.servlet.filter;

import scw.servlet.Request;
import scw.servlet.Response;

public interface Filter {
	void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
