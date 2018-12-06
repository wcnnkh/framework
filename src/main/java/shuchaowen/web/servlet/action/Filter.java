package shuchaowen.web.servlet.action;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public interface Filter {
	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
