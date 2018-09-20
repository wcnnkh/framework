package shuchaowen.web.servlet;

import shuchaowen.core.http.server.Filter;
import shuchaowen.core.http.server.FilterChain;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.http.server.Response;

public abstract class AbstractFilter implements Filter{
	public final void doFilter(Request request, Response response, FilterChain filterChain)
			throws Throwable {
		doFilter((WebRequest)request, (WebResponse)response, filterChain);
	}
	
	public abstract void doFilter(WebRequest request, WebResponse response, FilterChain filterChain) throws Throwable;
}
