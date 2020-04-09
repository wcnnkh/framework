package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.http.HttpAction;
import scw.mvc.action.http.HttpActionFilter;
import scw.mvc.http.HttpChannel;

public abstract class HttpServletFilter extends HttpActionFilter {

	@Override
	protected Object doHttpFilter(HttpChannel channel, HttpAction action,
			ActionFilterChain chain) throws Throwable {
		if(channel instanceof HttpServletChannel){
			HttpServletChannel httpServletChannel = (HttpServletChannel)channel;
			return doHttpServletFilter(httpServletChannel, httpServletChannel.getHttpServletRequest(), httpServletChannel.getHttpServletResponse(), action, chain);
		}
		
		return chain.doFilter(channel, action);
	}
	
	protected abstract Object doHttpServletFilter(HttpServletChannel channel, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpAction action, ActionFilterChain chain) throws Throwable;
}
