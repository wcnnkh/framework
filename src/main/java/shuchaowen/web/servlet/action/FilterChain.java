package shuchaowen.web.servlet.action;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;

public interface FilterChain{
	public void doFilter(Request request, Response response) throws Throwable;
}
